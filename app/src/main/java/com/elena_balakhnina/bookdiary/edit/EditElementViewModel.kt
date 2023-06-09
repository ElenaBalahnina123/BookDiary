package com.elena_balakhnina.bookdiary.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.launch
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.elena_balakhnina.bookdiary.BookEntity
import com.elena_balakhnina.bookdiary.BooksRepository
import com.elena_balakhnina.bookdiary.GenresRepository
import com.elena_balakhnina.bookdiary.ImageCache
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ARG_BOOK_ID = "book_id"
const val ARG_RATE_MODE = "allowRate"

@HiltViewModel
class EditElementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val booksRepository: BooksRepository,
    private val genresRepository: GenresRepository,
    @ApplicationContext
    private val context: Context,
    private val imageCache: ImageCache
) : ViewModel() {

    private val bookId = savedStateHandle.get<Long>(ARG_BOOK_ID)
    val rateMode = savedStateHandle.get<Boolean>(ARG_RATE_MODE) ?: false

    private val mutableState = MutableStateFlow(EditElementVmState())

    init {
        viewModelScope.launch {
            val genres = genresRepository.getAllGenres()

            Log.d("EDITOR","genres loaded: ${genres.size}")

            mutableState.value = mutableState.value.copy(
                genres = genres
            )

            if(bookId != null) {
                booksRepository.getById(bookId)?.let {
                    mutableState.value = EditElementVmState(
                        bookTitle = it.bookTitle,
                        author = it.author,
                        description = it.description.orEmpty(),
                        date = it.date,
                        rating = it.rating,
                        image = it.image,
                        allowRate = rateMode,
                        selectedGenreIndex = genres.indexOf(it.genre),
                    )
                }
            }
        }
    }

    fun bookTitleFlow() = mutableState.map { it.bookTitle }.distinctUntilChanged()

    fun authorFlow() = mutableState.map { it.author }.distinctUntilChanged()

    fun descriptionFlow() = mutableState.map { it.description }.distinctUntilChanged()

    fun genreFlow() = mutableState.map { it.selectedGenreIndex }

    fun ratingFlow() = mutableState.map { it.rating }

    fun dateFlow() = mutableState.map { it.date }

    fun saveClick(navController: NavController) {
        viewModelScope.launch {
            val state = mutableState.value
            if (state.bookTitle.isEmpty()) {
                Toast.makeText(context, "Введите название книги", Toast.LENGTH_SHORT).show()
                return@launch
            }
            if (state.author.isEmpty()) {
                Toast.makeText(context, "Введите автора", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if(rateMode) {
                if (state.rating == -1) {
                    Toast.makeText(context, "Введите рейтинг", Toast.LENGTH_SHORT).show()
                    return@launch

                }
            }

            val genre = state.genres.getOrNull(state.selectedGenreIndex)

            if (genre == null) {
                Toast.makeText(context, "Введите жанр", Toast.LENGTH_SHORT).show()
                return@launch
            }
                booksRepository.save(
                    BookEntity(
                        id = bookId,
                        bookTitle = state.bookTitle,
                        author = state.author,
                        description = state.description,
                        date = state.date,
                        rating = state.rating,
                        image = state.image,
                        genre = genre
                    )
                )
                navController.popBackStack()
        }
    }

    fun imageFlow() = mutableState.map { imageCache.getBitmapFromCache(it.image) }

    fun onTitleChange(newTitle: String) {
        mutableState.value = mutableState.value.copy(
            bookTitle = newTitle
        )
    }

    fun onAuthorChange(newAuthor: String) {
        mutableState.value = mutableState.value.copy(
            author = newAuthor
        )
    }

    fun onDescriptionChange(newDescription: String) {
        mutableState.value = mutableState.value.copy(
            description = newDescription
        )
    }

    fun onGenreSelected(genreIndex: Int) {
        mutableState.value = mutableState.value.copy(
            selectedGenreIndex = genreIndex
        )
    }

    fun onRatingSelected(rationg: Int) {
        mutableState.value = mutableState.value.copy(
            rating = rationg
        )
    }

    fun onDateChanged(date: Long) {
        mutableState.value = mutableState.value.copy(
            date = date
        )
    }

    fun onPickImageFromGallery(pickFromGalleryLauncher: ActivityResultLauncher<Intent>) {
        pickFromGalleryLauncher.launch(
            Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
        )
    }

    fun onImageFromGalleryPicked(activityResult: ActivityResult) {
        val data: Uri =
            activityResult.data?.data.takeIf { activityResult.resultCode == Activity.RESULT_OK }
                ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uuid = imageCache.saveImageFromUri(data)
                val oldUuid = mutableState.value.image
                if (oldUuid != null) {
                    imageCache.deleteImage(oldUuid)
                }
                mutableState.value = mutableState.value.copy(image = uuid)
            } catch (err: Throwable) {
                mutableState.value = mutableState.value.copy(image = null)
                Log.e("EditElement", "unable to save image from gallery", err)
            }
        }
    }

    fun onPhotoPicturePreviewReady(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val uuid = imageCache.saveImageFromBitmap(bitmap)
                val oldUuid = mutableState.value.image
                if (oldUuid != null) {
                    imageCache.deleteImage(oldUuid)
                }
                mutableState.value = mutableState.value.copy(image = uuid)
            } catch (err: Throwable) {
                mutableState.value = mutableState.value.copy(image = null)
                Log.e("EditElement", "unable to save image from camera", err)
            }
        }
    }

    fun onPickImageFromCamera(pickFromCameraLauncher: ActivityResultLauncher<Void?>) {
        pickFromCameraLauncher.launch()
    }
}