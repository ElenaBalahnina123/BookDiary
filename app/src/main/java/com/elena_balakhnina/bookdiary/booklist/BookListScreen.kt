package com.elena_balakhnina.bookdiary.booklist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elena_balakhnina.bookdiary.SearchAppbar
import com.elena_balakhnina.bookdiary.booklistitem.BookListItemData
import com.elena_balakhnina.bookdiary.booklistitem.BookListItemScreen
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class BookListScreenState(
    val books: List<BookListItemData>,
    val query: TextFieldValue,
)

@Preview
@Composable
fun BookListScreen(
    onAddClick: () -> Unit = {},
    stateFlow: Flow<BookListScreenState> = emptyFlow(),
    onBookClick: (Int) -> Unit = {},
    onToggleFavorite: (Int) -> Unit = {},
    onQueryChanged: (TextFieldValue) -> Unit = {},
) {

    BookDiaryTheme {
        val state by stateFlow.collectAsState(
            initial = BookListScreenState(
                emptyList(),
                TextFieldValue()
            )
        )

        Scaffold(
            topBar = {
                SearchAppbar(
                    searchText = state.query,
                    onSearchChanged = onQueryChanged
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            },
            ) { paddingValues ->

            LazyColumn(
                modifier = Modifier.padding(paddingValues),
            ) {
                if (state.books.isEmpty()) {
                    item {
                        Text(
                            text = "Нет прочитанных книг",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                } else {
                    itemsIndexed(
                        state.books,
                    ) { index, item ->
                        BookListItemScreen(
                            itemData = item,
                            onClick = { onBookClick(index) },
                            showRatingAndData = true,
                            onFavoriteToggle = { onToggleFavorite(index) }
                        )
                    }
                }
            }
        }

    }
}




