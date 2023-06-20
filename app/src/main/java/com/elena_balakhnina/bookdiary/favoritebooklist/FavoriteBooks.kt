package com.elena_balakhnina.bookdiary.favoritebooklist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.elena_balakhnina.bookdiary.compose.component.BookListItem
import com.elena_balakhnina.bookdiary.compose.component.BookListItemData
import com.elena_balakhnina.bookdiary.ui.theme.BookDiaryTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Preview
@Composable
fun FavoriteList(
    stateFlow: Flow<List<BookListItemData>> = emptyFlow(),
    onBookClick: (Int) -> Unit = {},
    onToggleFavorite: (Int)->Unit = {},
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
            ) { paddingValues ->

            val list by stateFlow.collectAsState(emptyList())

            LazyColumn(
                modifier = Modifier.padding(paddingValues),
            ) {
                itemsIndexed(list) { index, item ->
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
