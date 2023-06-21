package com.elena_balakhnina.bookdiary.editor

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.elena_balakhnina.bookdiary.domain.BookEntity
import com.elena_balakhnina.bookdiary.domain.BooksRepository
import com.elena_balakhnina.bookdiary.domain.GenresRepository
import com.elena_balakhnina.bookdiary.domain.ImageCache
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ARG_BOOK_ID = "book_id"
const val ARG_PLANNED_MODE = "allow_rate"
const val ARG_FAVORITE = "isFavorite"

@HiltViewModel
class EditElementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val booksRepository: BooksRepository,
    private val genresRepository: GenresRepository,
    @ApplicationContext
    private val context: Context,
    private val imageCache: ImageCache
) : ViewModel() {

    companion object {
        private const val TAG = "EditElementVM"
    }

    private val bookId = savedStateHandle.get<Long>(ARG_BOOK_ID)?.takeIf { it > 0 }
    private val plannedMode = savedStateHandle.get<Boolean>(ARG_PLANNED_MODE) ?: false

    private val mutableState = MutableStateFlow(
        EditElementVmState(
            plannedMode = plannedMode.also {
                Log.d(TAG, "rate mode: $it")
            }
        )
    )

    init {
        Log.d(TAG, "rateMode: $plannedMode")
        Log.d(TAG, "bookId: $bookId")

        viewModelScope.launch {
            val genres = genresRepository.getAllGenres()

            Log.d(TAG, "genres loaded: ${genres.size}")

            mutableState.value = bookId?.let { booksRepository.getById(it) }?.let {
                mutableState.value.copy(
                    bookTitle = TextFieldValue(it.bookTitle),
                    author = TextFieldValue(it.author),
                    description = TextFieldValue(it.description.orEmpty()),
                    date = it.date,
                    rating = it.rating,
                    image = it.image,
                    plannedMode = plannedMode,
                    selectedGenreIndex = genres.indexOf(it.genre),
                    genres = genres
                )
            } ?: kotlin.run {
                mutableState.value.copy(
                    genres = genres
                )
            }
        }
    }

    fun saveClick(navController: NavController) {
        viewModelScope.launch {
            val state = mutableState.value
            if (state.bookTitle.text.isEmpty()) {
                Toast.makeText(context, "Введите название книги", Toast.LENGTH_SHORT).show()
                return@launch
            }
            if (state.author.text.isEmpty()) {
                Toast.makeText(context, "Введите автора", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if (plannedMode) {
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
                    bookTitle = state.bookTitle.text,
                    author = state.author.text,
                    description = state.description.text,
                    date = state.date,
                    rating = state.rating,
                    image = state.image,
                    genre = genre,
                    plannedMode = state.plannedMode,
                    isFavorite = state.isFavorite
                )
            )
            navController.popBackStack()
        }
    }

    val uiFlow = mutableState.map { viewModelState ->
        EditElementData(
            bookTitle = viewModelState.bookTitle,
            selectedGenreIndex = viewModelState.selectedGenreIndex,
            plannedMode = viewModelState.plannedMode,
            date = viewModelState.date,
            rating = viewModelState.rating,
            image = imageCache.getBitmapFromCache(viewModelState.image),
            description = viewModelState.description,
            author = viewModelState.author,
            genres = viewModelState.genres.map { genreDBEntity -> genreDBEntity.genre },
            isFavorite = viewModelState.isFavorite
        )
    }.distinctUntilChanged().onEach {
        Log.d(TAG, "update uiFlow to $it")
    }.stateIn(
        viewModelScope, SharingStarted.Eagerly, EditElementData(
            bookTitle = TextFieldValue(),
            author = TextFieldValue(),
            image = null,
            isFavorite = false,
            plannedMode = false,
            genres = emptyList(),
            description = TextFieldValue(),
            rating = 0,
            date = 0L,
            selectedGenreIndex = -1
        )
    )

    fun onTitleChange(newTitle: TextFieldValue) {
        Log.d(TAG, "onTitleChange: ${newTitle.text}")
        mutableState.value = mutableState.value.copy(
            bookTitle = newTitle
        )
    }

    fun onAuthorChange(newAuthor: TextFieldValue) {
        Log.d(TAG, "onAuthorChange: ${newAuthor.text}")
        mutableState.value = mutableState.value.copy(
            author = newAuthor
        )
    }

    fun onDescriptionChange(newDescription: TextFieldValue) {
        Log.d(TAG, "onDescriptionChange: ${newDescription.text}")
        mutableState.value = mutableState.value.copy(
            description = newDescription
        )
    }

    fun onGenreSelected(genreIndex: Int) {
        Log.d(TAG, "onGenreSelected: $genreIndex")
        mutableState.value = mutableState.value.copy(
            selectedGenreIndex = genreIndex
        )
    }

    fun onRatingSelected(rating: Int) {
        Log.d(TAG, "onRatingSelected: $rating")
        mutableState.value = mutableState.value.copy(
            rating = rating
        )
    }

    fun onDateChanged(date: Long) {
        mutableState.value = mutableState.value.copy(
            date = date
        )
    }

    fun onPickImageFromGallery(pickFromGalleryLauncher: ActivityResultLauncher<Intent>) {
        Log.d(TAG, "onPickImageFromGallery")
        pickFromGalleryLauncher.launch(
            Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
        )
    }

    fun onImageFromGalleryPicked(activityResult: ActivityResult) {
        Log.d(TAG, "onImageFromGalleryPicked")
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
                Log.e("EditElementScreen", "unable to save image from gallery", err)
            }
        }
    }

    fun onPhotoPicturePreviewReady(bitmap: Bitmap) {
        Log.d(TAG, "onPhotoPicturePreviewReady")
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
                Log.e("EditElementScreen", "unable to save image from camera", err)
            }
        }
    }

    fun onPickImageFromCamera(pickFromCameraLauncher: ActivityResultLauncher<Void?>) {
        Log.d(TAG, "onPickImageFromCamera")
        pickFromCameraLauncher.launch()
    }
}