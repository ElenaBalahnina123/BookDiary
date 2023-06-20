package com.elena_balakhnina.bookdiary.edit

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elena_balakhnina.bookdiary.R
import com.elena_balakhnina.bookdiary.compose.component.Calendar
import com.elena_balakhnina.bookdiary.compose.component.DropdownComponent
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme

data class EditElementData(
    val bookTitle: TextFieldValue,
    val author: TextFieldValue,
    val image: ImageBitmap?,
    val description: TextFieldValue,
    val selectedGenreIndex: Int,
    val genres: List<String>,
    val rating: Int,
    val date: Long,
    val allowRate: Boolean,
    val isFavorite : Boolean
)

@Composable
fun EditElement(
    data: EditElementData,
    onSaveClick: () -> Unit = {},
    onTitleChange: (TextFieldValue) -> Unit = {},
    onAuthorChange: (TextFieldValue) -> Unit = {},
    onClickGallery: () -> Unit = {},
    onClickCamera: () -> Unit = {},
    onDescriptionChange: (TextFieldValue) -> Unit = {},
    onGenreChange: (Int) -> Unit = {},
    onRatingChanged: (Int) -> Unit = {},
    onDateChanged: (Long) -> Unit = {},
    onPopBackStack: () -> Unit = {}
) {
    BookDiaryTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Book diary",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 30.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onPopBackStack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onSaveClick) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                }
            }) { scaffoldPaddings ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPaddings)
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Log.d("OLOLO","recomposition, data: $data")

                TextField(
                    value = data.bookTitle,
                    onValueChange = onTitleChange,
                    label = {
                        Text(text = "Название книги")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    )
                )

                TextField(
                    value = data.author,
                    onValueChange = onAuthorChange,
                    label = {
                        Text(text = "Автор")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    )
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(0.45f)
                            .aspectRatio(0.65f)
                            .padding(top = 8.dp)
                    ) {
                        data.image?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } ?: kotlin.run {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray)
                            )
                        }
                    }

                    Row(Modifier.padding(8.dp)) {
                        Button(
                            onClick = onClickGallery,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.gallery),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(24.dp),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Button(onClick = onClickCamera) {
                            Image(
                                painter = painterResource(id = R.drawable.camera),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(24.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
//
                if (data.allowRate) {
                    val ratings: List<String> = remember {
                        (1..10).map {
                            it.toString()
                        }
                    }
                    DropdownComponent(
                        options = ratings,
                        hint = "Рейтинг",
                        selectedOption = data.rating,
                        onSelectedOptionChange = { onRatingChanged(it + 1) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                DropdownComponent(
                    options = data.genres,
                    hint = "Жанр",
                    selectedOption = data.selectedGenreIndex,
                    onSelectedOptionChange = onGenreChange
                )

                if (data.allowRate) {
                    Calendar(
                        date = data.date,
                        onDateChanged = onDateChanged,
                    )
                }

                TextField(
                    value = data.description,
                    onValueChange = onDescriptionChange,
                    label = {
                        Text(text = "Описание")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    )
                )
            }
        }
    }
}










