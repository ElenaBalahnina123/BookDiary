package com.elena_balakhnina.bookdiary

import android.util.Log
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewElementVM(
    private val bookId: Long,
) : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(ViewElementVmState())

    val stateFlow get() = mutableStateFlow.asStateFlow()

    init {
        Log.d("ViewElementVM", "book id: $bookId")
    }

    fun onDelete() {

    }

}

data class ViewElementVmState(
    val bookTitle: String = "",
    val author: String = "",
    val description: String = "",
    val date: Long = 0,
    val rating: Int = 0,
    val genre: String = "",
)

@Preview
@Composable
fun ViewElement(
    navController: NavController = rememberNavController(),
    onEditClick: () -> Unit = {},
    onDelete: () -> Unit = {},
    state: ViewElementVmState = ViewElementVmState(
        bookTitle = "book title",
        author = "author",
        description = "description",
        date = System.currentTimeMillis(),
        rating = 3,
        genre = "genre"
    )
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
                modifier = Modifier
                    .padding(it)
                    .padding(12.dp)
            ) {

                Text(
                    text = state.bookTitle,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = state.author,
                    color = Color.Blue,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row() {
                    Image(
                        painter = painterResource(id = R.drawable.kingdom),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .aspectRatio(0.65f),
                        contentScale = ContentScale.Crop
                    )

                    Column() {
                        Row(modifier = Modifier.padding(start = 48.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.star),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(50.dp)
                                    .aspectRatio(0.7f)
                            )
                            Text(
                                text = state.rating.toString(),
                                fontSize = 46.sp
                            )
                        }
                        Text(
                            text = "Жанр: ${state.genre}",
                            modifier = Modifier.padding(start = 18.dp, top = 18.dp)
                        )
                        Text(
                            text = String.format("%1\$td.%1\$tm.%1\$ty", state.date),
                            modifier = Modifier.padding(start = 18.dp, top = 18.dp)
                        )
                    }

                }
                Text(text = state.description, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}