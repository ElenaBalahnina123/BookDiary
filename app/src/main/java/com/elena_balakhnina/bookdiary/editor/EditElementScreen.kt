package com.elena_balakhnina.bookdiary.editor

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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.elena_balakhnina.bookdiary.FlowTextField
import com.elena_balakhnina.bookdiary.R
import com.elena_balakhnina.bookdiary.collectMappedState
import com.elena_balakhnina.bookdiary.compose.component.Calendar
import com.elena_balakhnina.bookdiary.compose.component.DropDownComponent
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


@Composable
fun EditElementScreen(
    flow: Flow<EditElementData> = emptyFlow(),

    onTitleChange: (TextFieldValue) -> Unit = {},
    onAuthorChange: (TextFieldValue) -> Unit = {},
    onDescriptionChange: (TextFieldValue) -> Unit = {},

    onClickGallery: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onClickCamera: () -> Unit = {},
    onGenreChange: (Int) -> Unit = {},
    onRatingChanged: (Int) -> Unit = {},
    onDateChanged: (Long) -> Unit = {},
    onPopBackStack: () -> Unit = {},
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
            }
        ) { scaffoldPaddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPaddings)
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                FlowTextField(
                    flow = remember {
                        flow.map { it.bookTitle }
                            .distinctUntilChanged()
                            .flowOn(Dispatchers.IO)
                    },
                    onValueChange = onTitleChange,
                    label = {
                        Text(text = "Название книги")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    )
                )
                FlowTextField(
                    flow = remember {
                        flow.map { it.author }
                            .distinctUntilChanged()
                            .flowOn(Dispatchers.IO)
                    },
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
                        val image: ImageBitmap? by flow.collectMappedState(
                            initialValue = null
                        ) { it.image }

                        image?.let {
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
                                painter = painterResource(id = R.drawable.collections_white_24dp),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(24.dp),
                                contentScale = ContentScale.Inside,
                            )
                        }
                        Button(onClick = onClickCamera) {
                            Image(
                                painter = painterResource(id = R.drawable.add_a_photo_white_24dp),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(24.dp),
                                contentScale = ContentScale.Inside
                            )
                        }
                    }
                }

                DropDownComponent(
                    onSelectedOptionChange = onGenreChange,
                    flow = remember {
                        flow.map { it.genresDropDownState }
                            .distinctUntilChanged()
                            .flowOn(Dispatchers.IO)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                val isPlannedMode by remember {
                    flow.map { it.plannedMode }
                        .distinctUntilChanged()
                        .flowOn(Dispatchers.IO)
                }.collectAsState(initial = false)

                if (isPlannedMode) {
                    DropDownComponent(
                        flow = remember {
                            flow.map { it.ratingState }
                                .distinctUntilChanged()
                                .flowOn(Dispatchers.IO)
                        },
                        onSelectedOptionChange = { onRatingChanged(it + 1) }
                    )

                    Calendar(
                        dateFlow = remember {
                            flow.map { it.date }
                                .distinctUntilChanged()
                                .flowOn(Dispatchers.IO)
                        },
                        onDateChanged = onDateChanged,
                    )
                }

                FlowTextField(
                    flow = remember {
                        flow.map { it.description }
                            .distinctUntilChanged()
                            .flowOn(Dispatchers.IO)
                    },
                    onValueChange = onDescriptionChange,
                    label = {
                        Text(text = "Описание")
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    )
                )

                Button(
                    onClick = onSaveClick, modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    Text(text = "Сохранить")
                }
            }
        }
    }
}











