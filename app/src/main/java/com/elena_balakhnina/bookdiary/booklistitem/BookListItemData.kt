package com.elena_balakhnina.bookdiary.booklistitem

import androidx.compose.ui.graphics.ImageBitmap

data class BookListItemData(
    val bookTitle: String,
    val author: String,
    val description: String,
    val date: String?,
    val rating: Int?,
    val genre: String,
    val image: ImageBitmap?,
    val showRatingAndData: Boolean,
    val isFavorite: Boolean
)