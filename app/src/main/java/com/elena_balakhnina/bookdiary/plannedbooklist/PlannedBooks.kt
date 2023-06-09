package com.elena_balakhnina.bookdiary.plannedbooklist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.elena_balakhnina.bookdiary.BookListItem
import com.elena_balakhnina.bookdiary.BookItemData
import com.elena_balakhnina.bookdiary.BookListItemData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun PlannedBooks(
    onAddPlannedBook: () -> Unit = {},
    onBookClick: (Int) -> Unit = {},
    stateFlow: Flow<List<BookListItemData>> = emptyFlow(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Book diary", fontFamily = FontFamily.Cursive, fontSize = 30.sp)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlannedBook,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },

        ) { paddingValues ->

        val state = stateFlow.collectAsState(emptyList())

        LazyColumn(
            modifier = Modifier.padding(paddingValues),
        ) {
            itemsIndexed(state.value) { index, item ->
                BookListItem(itemData = item, onClick = {
                    onBookClick(index)
                }, showRatingAndData = false)
            }
        }
    }
}