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
import com.elena_balakhnina.bookdiary.booklistitem.BookListItem
import com.elena_balakhnina.bookdiary.booklistitem.BookListItemData
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow


@Preview
@Composable
fun BookListScreen(
    searchFlow: StateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue()),
    booksListFlow: Flow<List<BookListItemData>> = emptyFlow(),
    onAddClick: () -> Unit = {},
    onBookClick: (Int) -> Unit = {},
    onToggleFavorite: (Int) -> Unit = {},
    onQueryChanged: (TextFieldValue) -> Unit = {},
) {

    BookDiaryTheme {
        Scaffold(
            topBar = {
                val query by searchFlow.collectAsState()
                SearchAppbar(
                    searchText = query,
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

            val books by booksListFlow.collectAsState(emptyList())

            LazyColumn(
                modifier = Modifier.padding(paddingValues),
            ) {
                if (books.isEmpty()) {
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
                        books,
                    ) { index, item ->
                        BookListItem(
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




