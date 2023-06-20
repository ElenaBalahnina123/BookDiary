package com.elena_balakhnina.bookdiary

import androidx.compose.ui.graphics.ImageBitmap

data class BookItemData(
    val bookId: Long,
    val bookTitle: String,
    val author: String,
    val description: String,
    val date: Long?,
    val rating: Int?,
    val genre: String,
    val image: ImageBitmap?,
    val rate : Boolean,
    val isFavorite : Boolean
)