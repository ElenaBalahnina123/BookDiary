package com.elena_balakhnina.bookdiary

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.elena_balakhnina.bookdiary.edit.ARG_RATE_MODE
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    private val rateMode = savedStateHandle.get<Boolean>(ARG_RATE_MODE) ?: false

    private val mutableState = MutableStateFlow<BookEntity?>(null)

    init {
        if (bookId != null) {
            viewModelScope.launch {
                repository.getById(bookId)?.let {
                    mutableState.value = it
                }
            }
        }
    }

    fun uiFlow(): Flow<ViewElementScreenData> = mutableState
        .filterNotNull()
        .map {
            ViewElementScreenData(
                bookTitle = it.bookTitle,
                author = it.author,
                description = it.description.orEmpty(),
                date = it.date,
                rating = it.rating,
                genre = it.genre.genre,
                image = imageCache.getBitmapFromCache(it.image),
                allowRate = rateMode,
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

data class ViewElementScreenData(
    val bookTitle: String = "",
    val author: String = "",
    val description: String = "",
    val date: Long = -1,
    val rating: Int = -1,
    val genre: String = "",
    val image: ImageBitmap? = null,
    val allowRate: Boolean = false,
)


@Composable
fun ViewElementScreen(
    navController: NavController = rememberNavController(),
    onEditClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    viewElementScreenData: ViewElementScreenData,
) {
    BookDiaryTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(text = "Book diary", fontFamily = FontFamily.Cursive, fontSize = 30.sp)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = onEditClick
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
                    }
                }
            )
        }) {
            Column(
                modifier = Modifier.padding(it).padding(12.dp)
            ) {

                Box {

                    Text(
                        text = viewElementScreenData.bookTitle,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Box() {
                    Text(
                        text = viewElementScreenData.author,
                        color = Color.Blue,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                Row() {
                    Box() {

                        viewElementScreenData.image?.let { imageBitmap ->
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth(0.45f)
                                    .aspectRatio(0.65f),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Column() {
                        if (viewElementScreenData.allowRate) {
                            Row(modifier = Modifier.padding(start = 48.dp)) {
                                Image(
                                    painter = painterResource(id = R.drawable.star),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(50.dp)
                                        .aspectRatio(0.7f)
                                )
                                Box {
                                    Text(
                                        text = viewElementScreenData.rating.toString(),
                                        fontSize = 46.sp
                                    )
                                }
                            }
                        }
                        Box() {
                            Text(
                                text = "Жанр: ${viewElementScreenData.genre}",
                                modifier = Modifier.padding(start = 18.dp, top = 18.dp)
                            )
                        }
                        if (viewElementScreenData.allowRate) {
                            Box() {
                                Text(
                                    text = String.format(
                                        "%1\$td.%1\$tm.%1\$ty",
                                        viewElementScreenData.date
                                    ),
                                    modifier = Modifier.padding(start = 18.dp, top = 18.dp)
                                )
                            }
                        }

                    }
                }
                Box() {

                    Text(
                        text = viewElementScreenData.description,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

            }
        }
    }
}