package com.elena_balakhnina.bookdiary

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BookListViewModel : ViewModel() {
    fun onAddClick(navController: NavController) {
        navController.navigate("books/new")
    }

    private val mutableStateFlow = MutableStateFlow(BookListVmState())

    val stateFlow get() = mutableStateFlow.asStateFlow()

    fun onItemClick(bookItemData: BookItemData) {

    }
}

data class BookListVmState(
    val books: List<BookItemData> = BookItemDataPreviewProvider()
        .values.take(3).toList()
)

@Preview
@Composable
fun BookList(
    navController: NavController = rememberNavController(),
    viewModel: BookListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    BookDiaryTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Book diary", fontFamily = FontFamily.Cursive, fontSize = 30.sp)
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    viewModel.onAddClick(navController)
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }
        ) {

            val state = viewModel.stateFlow.collectAsState()

            LazyColumn(
                modifier = Modifier.padding(it),
            ) {
                items(state.value.books) {
                    ItemList(itemData = it, onClick = {
                        // viewModel::onItemClick
                        navController.navigate("books/1")
                    })
                }
            }
        }
    }
}