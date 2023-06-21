package com.elena_balakhnina.bookdiary.booklist

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.elena_balakhnina.bookdiary.BookItemData
import com.elena_balakhnina.bookdiary.booklistitem.BookListItemData
import com.elena_balakhnina.bookdiary.domain.BooksRepository
import com.elena_balakhnina.bookdiary.domain.ImageCache
import com.elena_balakhnina.bookdiary.editor.ARG_PLANNED_MODE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val cache: ImageCache,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val plannedMode = savedStateHandle.get<Boolean>(ARG_PLANNED_MODE) ?: false

    private val mutableStateFlow = MutableStateFlow(BookListVmState())

    init {
        viewModelScope.launch {
            booksRepository.ratedBooksFlow().collect { bookEntities ->
                Log.d("BookListViewModel", "got rated book list, count: $bookEntities")
                mutableStateFlow.value = mutableStateFlow.value.copy(
                    books = bookEntities.map {
                        BookItemData(
                            bookTitle = it.bookTitle,
                            author = it.author,
                            description = it.description.orEmpty(),
                            date = it.date,
                            rating = it.rating,
                            genre = it.genre.genre,
                            image = cache.getBitmapFromCache(it.image),
                            bookId = requireNotNull(it.id),
                            plannedMode = plannedMode,
                            isFavorite = it.isFavorite
                        )
                    }
                )
            }
        }
    }

    fun booksFlow(): Flow<List<BookListItemData>> {
        return mutableStateFlow.map { bookListVmState ->
            bookListVmState.books.map {
                BookListItemData(
                    bookTitle = it.bookTitle,
                    author = it.author,
                    description = it.description,
                    date = String.format("%1\$td.%1\$tm.%1\$ty", it.date),
                    rating = it.rating,
                    genre = it.genre,
                    image = it.image,
                    showRatingAndData = plannedMode,
                    isFavorite = it.isFavorite
                )
            }
        }
    }

    fun onBookClick(it: Int, navController: NavHostController) {
        val book = mutableStateFlow.value.books[it]
        navController.navigate("books/${book.bookId}")
    }

    fun onToggleFavorite(it: Int) {
        Log.d("BookListViewModel", "onToggleFavorite $it")
        val book = mutableStateFlow.value.books[it]
        viewModelScope.launch {
            booksRepository.setFavorite(book.bookId, !book.isFavorite)
        }
    }

}

