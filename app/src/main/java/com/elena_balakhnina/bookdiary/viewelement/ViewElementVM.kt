package com.elena_balakhnina.bookdiary.viewelement

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.elena_balakhnina.bookdiary.domain.BookEntity
import com.elena_balakhnina.bookdiary.domain.BooksRepository
import com.elena_balakhnina.bookdiary.domain.ImageCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewElementVM @Inject constructor(
    //позволяет сохранять и загружать какие-либо данные. Будет переживать пересоздание view модели
    savedStateHandle: SavedStateHandle,
    private val repository: BooksRepository,
    private val imageCache: ImageCache,
) : ViewModel() {
    private val bookId = savedStateHandle.get<Long>("book_id")
    private val plannedMode = savedStateHandle.get<Boolean>("planned_mode") ?: false

    private val mutableState = MutableStateFlow<BookEntity?>(null)

    init {
        Log.d("ViewElement", "plannedMode: $plannedMode")

        if (bookId != null) {
            viewModelScope.launch {
                repository.getById(bookId)?.let {
                    mutableState.value = it
                } ?: kotlin.run {
                    Log.e("ViewElement", "book $bookId not found")
                }
            }
        } else {
            Log.e("ViewElement", "bookId not set")
        }
    }
    fun uiFlow(): Flow<ViewElementData> = mutableState
        .filterNotNull()
        .map {
            ViewElementData(
                bookTitle = it.bookTitle,
                author = it.author,
                description = it.description.orEmpty(),
                date = it.date,
                rating = it.rating,
                genre = it.genre.genre,
                image = imageCache.getBitmapFromCache(it.image),
                allowRate = !plannedMode,
            )
        }

    fun onDelete(navController: NavController) {
        viewModelScope.launch {
            if (bookId != null) {
                repository.delete(bookId)
            }
            navController.popBackStack()
        }
    }
}