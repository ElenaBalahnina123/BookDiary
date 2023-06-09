package com.elena_balakhnina.bookdiary.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.elena_balakhnina.bookdiary.Genre
import com.elena_balakhnina.bookdiary.R
import com.elena_balakhnina.bookdiary.compose.component.Calendar
import com.elena_balakhnina.bookdiary.compose.component.DropdownComponent
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import kotlinx.coroutines.flow.*

@Preview
@Composable
fun EditElement(
//    navController: NavController = rememberNavController(),
    onSaveClick: () -> Unit = {},
    bookTitleFlow: Flow<String> = emptyFlow(),
    onTitleChange: (String) -> Unit = {},
    authorFlow: Flow<String> = emptyFlow(),
    onAuthorChange: (String) -> Unit = {},
    imageFlow: Flow<ImageBitmap?> = emptyFlow(),
    onClickGallery: () -> Unit = {},
    onClickCamera: () -> Unit = {},
    descriptionFlow: Flow<String> = emptyFlow(),
    onDescriptionChange: (String) -> Unit = {},

    selectedGenreIndexFlow: Flow<Int> = emptyFlow(),
    onGenreChange: (Int) -> Unit = {},
    genres: List<String> = emptyList(),

    ratingFlow: Flow<Int> = emptyFlow(),
    onRatingChanged: (Int) -> Unit = {},
    initialDate: Long = System.currentTimeMillis(),
    dateFlow: Flow<Long> = emptyFlow(),
    onDateChanged: (Long) -> Unit = {},
    allowRate: Boolean = false,
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

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(scaffoldPaddings)
                    .padding(8.dp)
                    .verticalScroll(scrollState),
            ) {
                Box {
                    val title by bookTitleFlow.collectAsState(initial = "")

                    TextField(
                        value = title,
                        onValueChange = onTitleChange,
                        label = {
                            Text(text = "Название книги")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent
                        )
                    )
                }
                Box {
                    val author by authorFlow.collectAsState(initial = "")

                    TextField(
                        value = author,
                        onValueChange = onAuthorChange,
                        label = {
                            Text(text = "Автор")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent
                        )
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {

                    val bitmap by imageFlow.collectAsState(initial = null)

                    Box(
                        Modifier
                            .fillMaxWidth(0.45f)
                            .aspectRatio(0.65f)
                            .padding(top = 8.dp)
                    ) {
                        bitmap?.let {
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

                if (allowRate) {
                    Box {
                        val selectedOptionIndex by produceState(initialValue = -1) {
                            ratingFlow.collect {
                                value = (it - 1).coerceIn(-1..10)
                            }
                        }
                        val ratings: List<String> = remember {
                            (1..10).map {
                                it.toString()
                            }
                        }
                        DropdownComponent(
                            options = ratings,
                            hint = "Рейтинг",
                            selectedOption = selectedOptionIndex,
                            onSelectedOptionChange = { onRatingChanged(it + 1) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Box {
                    val selectedOption by selectedGenreIndexFlow.collectAsState(initial = 0)
                    DropdownComponent(
                        options = genres,
                        hint = "Жанр",
                        selectedOption = selectedOption,
                        onSelectedOptionChange = onGenreChange
                    )
                }

                if (allowRate) {
                    Calendar(
                        dateFlow = dateFlow,
                        onDateChanged = onDateChanged,
                    )
                }

                Box() {
                    val description by descriptionFlow.collectAsState(initial = "")

                    TextField(
                        value = description,
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
}

//val Genres = listOf(
//    "Авангардная литература",
//    "Бизнес",
//    "Биография",
//    "Боевик",
//    "Вестерн",
//    "Воспитание",
//    "Детектив",
//    "Детская литература",
//    "Журнал, газета",
//    "Здоровье",
//    "Искусство",
//    "Исторический роман",
//    "Комикс, манга",
//    "Классика",
//    "Любовный роман",
//    "Мистика",
//    "Мифы и легенды",
//    "Мода и красота",
//    "Наука",
//    "Научная фантастика",
//    "Питание и кулинария",
//    "Повесть",
//    "Политика, экономика и право",
//    "Поэзия",
//    "Приключения",
//    "Психология",
//    "Роман",
//    "Сказка",
//    "Современная литература",
//    "Техника",
//    "Триллер",
//    "Ужасы",
//    "Учебная литература",
//    "Фантастика",
//    "Философия",
//    "Фэнтези",
//    "Энциклопедия",
//    "Юмор",
//    "18+",
//)









