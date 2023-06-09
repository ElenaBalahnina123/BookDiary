package com.elena_balakhnina.bookdiary.plannedbooklist

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elena_balakhnina.bookdiary.edit.ARG_RATE_MODE
import com.elena_balakhnina.bookdiary.BookItemData
import com.elena_balakhnina.bookdiary.BookListItemData
import com.elena_balakhnina.bookdiary.BooksRepository
import com.elena_balakhnina.bookdiary.ImageCache
import com.elena_balakhnina.bookdiary.booklist.BookListVmState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModelPlanned @Inject constructor(
    private val booksRepository: BooksRepository,
    private val cache: ImageCache,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val rateMode = savedStateHandle.get<Boolean>(ARG_RATE_MODE) ?: false

    private val mutableStateFlow = MutableStateFlow(BookListVmState())

    init {
            viewModelScope.launch {
                Log.d("OLOLO", rateMode.toString())
                booksRepository.plannedBooksFlow().collect { bookEntities ->
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
                                rate = rateMode
                            )
                        }
                    )


            }
        }
    }

    fun booksFlow(): Flow<List<BookListItemData>> {
        return mutableStateFlow.map {
            it.books.map {
                BookListItemData(
                    bookTitle = it.bookTitle,
                    author = it.author,
                    description = it.description,
                    date = it.date.toString(),
                    rating = it.rating,
                    genre = it.genre,
                    image = it.image,
                    rate = rateMode
                )
            }
        }
    }
}
