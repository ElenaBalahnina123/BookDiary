package com.elena_balakhnina.bookdiary

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditElementViewModel @Inject constructor(
    //private val bookId: Long? = null
    savedStateHandle: SavedStateHandle,
    repository: BooksRepository
) : ViewModel() {


    init {
        savedStateHandle.
    }

    private val mutableState = MutableStateFlow(EditElementVmState())
    val stateFlow get() = mutableState.asStateFlow()

    fun bookTitleFlow() = mutableState.map { it.bookTitle }.distinctUntilChanged()

    fun authorFlow() = mutableState.map { it.author }.distinctUntilChanged()

    fun descriptionFlow() = mutableState.map { it.description }.distinctUntilChanged()

    fun saveClick() {

    }

    fun onClickGallery() {

    }

    fun onClickCamera() {

    }

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

}

data class EditElementVmState(
    val bookTitle: String = "",
    val author: String = "",
    val description: String = "",
    val date: Long = 0,
    val rating: Int = 0,
    val genre: String = "",
    val image: String = ""
)

@Preview
@Composable
fun EditElement(
    navController: NavController = rememberNavController(),
    viewModel: EditElementViewModel = EditElementViewModel()
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
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = viewModel::saveClick) {
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
                    val title by viewModel.bookTitleFlow().collectAsState(initial = "")

                    TextField(
                        value = title,
                        onValueChange = viewModel::onTitleChange,
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
                    val author by viewModel.authorFlow().collectAsState(initial = "")

                    TextField(
                        value = author,
                        onValueChange = viewModel::onAuthorChange,
                        label = {
                            Text(text = "Автор")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent
                        )
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.rectangle),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.45f)
                        .aspectRatio(0.65f)
                        .padding(8.dp),
                    contentScale = ContentScale.Crop,

                    )

                Row(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = viewModel::onClickGallery,
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

                    Button(onClick = viewModel::onClickCamera) {
                        Image(
                            painter = painterResource(id = R.drawable.camera),
                            contentDescription = null,
                            modifier = Modifier
                                .width(24.dp),
                            contentScale = ContentScale.Crop

                        )
                    }
                }


                DropDownMenu()
                DropDownMenuGenre()
                Calendar()

                Box() {
                    val description by viewModel.descriptionFlow().collectAsState(initial = "")

                    TextField(
                        value = description,
                        onValueChange = viewModel::onDescriptionChange,
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


@Composable
fun DropDownMenu() {
    var expanded by remember { mutableStateOf(false) }
    val suggestions = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    var selectedText by remember { mutableStateOf("") }

    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown


    Column(Modifier.padding(0.dp)) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textfieldSize = coordinates.size.toSize()
                },
            label = { Text("Рейтинг") },
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textfieldSize.width.toDp() })
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedText = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }

}

@Composable
fun DropDownMenuGenre() {

    var expanded by remember { mutableStateOf(false) }
    val suggestions = listOf(
        "Авангардная литература",
        "Боевик",
        "Детектив",
        "Исторический роман",
        "Любовный роман",
        "Мистика",
        "Приключения",
        "Триллер/ужасы",
        "Фантастика",
        "Фэнтези"
    )
    var selectedText by remember { mutableStateOf("") }

    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown


    Column(Modifier.padding(0.dp)) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textfieldSize = coordinates.size.toSize()
                },
            label = { Text("Жанр") },
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textfieldSize.width.toDp() })
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedText = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}

class CalendarController(
    year: Int,
    month: Int,
    day: Int,
    private val context: Context,
) {
    private val mutableDateState = MutableStateFlow(Triple(year, month, day))

    val dateState get() = mutableDateState.asStateFlow()

    fun showDialog() {
        val currentState = mutableDateState.value

        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                mutableDateState.value = Triple(year, month, dayOfMonth)
            }, currentState.first, currentState.second+1, currentState.third
        ).show()
    }
}

@Composable
fun rememberCalendarController(
    year: Int = -1,
    month: Int = -1,
    day: Int = -1
): CalendarController {
    val context = LocalContext.current
    return remember {
        val calendar = Calendar.getInstance()
        if (year != -1) {
            calendar.set(Calendar.YEAR, year)
        }
        if (month != -1) {
            calendar.set(Calendar.MONTH, month)
        }
        if (day != -1) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
        }

        CalendarController(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH),
            day = calendar.get(Calendar.DAY_OF_MONTH),
            context = context
        )
    }
}

@Composable
fun Calendar() {
    val controller = rememberCalendarController()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        val currentDate by controller.dateState.collectAsState()


        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = controller::showDialog) {
                Text(
                    text = "${currentDate.third}.${currentDate.second}.${currentDate.first}",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif
                )
            }
            Icon(imageVector = Icons.Default.DateRange, contentDescription = "calendar")
        }
    }
}





