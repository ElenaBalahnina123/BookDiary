package com.elena_balakhnina.bookdiary.booklist

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.elena_balakhnina.bookdiary.BookItemData
import com.elena_balakhnina.bookdiary.booklistitem.BookListItemData
import com.elena_balakhnina.bookdiary.domain.BookEntity
import com.elena_balakhnina.bookdiary.domain.BooksRepository
import com.elena_balakhnina.bookdiary.domain.ImageCache
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val cache: ImageCache,
    @ApplicationContext
    private val context: Context,
) : ViewModel() {

    private val mutableStateFlow = MutableStateFlow(TextFieldValue())

    val searchFlow get() = mutableStateFlow.asStateFlow()

    private val booksStateFlow = mutableStateFlow
        .transformLatest { query ->
            booksRepository.getRatedBooksWithQuery(query.text).collect {
                emit(it.map { it.toBookItemData() })
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun booksFlow(): Flow<List<BookListItemData>> =
        booksStateFlow
            .map { it.map { it.toBookListItemData() } }
            .flowOn(Dispatchers.IO)

    fun onQueryChanged(query: TextFieldValue) {
        mutableStateFlow.value = query
    }

    private suspend fun BookEntity.toBookItemData(): BookItemData {
        return BookItemData(
            bookTitle = bookTitle,
            author = author,
            description = description.orEmpty(),
            date = date,
            rating = rating,
            genre = genre.genre,
            image = cache.getBitmapFromCache(image),
            bookId = requireNotNull(id),
            plannedMode = plannedMode,
            isFavorite = isFavorite
        )
    }

    private fun BookItemData.toBookListItemData(): BookListItemData {
        return BookListItemData(
            bookTitle = bookTitle,
            author = author,
            description = description,
            date = String.format("%1\$td.%1\$tm.%1\$ty", date),
            rating = rating,
            genre = genre,
            image = image,
            showRatingAndData = plannedMode,
            isFavorite = isFavorite
        )
    }

    fun onBookClick(bookIndex: Int, navController: NavHostController) {
        val book = booksStateFlow.value[bookIndex]
        navController.navigate("books/${book.bookId}")
    }

    fun onToggleFavorite(bookIndex: Int) {
        Log.d("BookListViewModel", "onToggleFavorite $bookIndex")
        val book = booksStateFlow.value[bookIndex]
        viewModelScope.launch {
            booksRepository.setFavorite(book.bookId, !book.isFavorite)
            if (!book.isFavorite) {
                Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

