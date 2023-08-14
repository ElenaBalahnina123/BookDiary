package com.elena_balakhnina.bookdiary.editor

import androidx.compose.ui.graphics.ImageBitmap

data class EditElementData(
    val bookTitle: String,
    val author: String,
    val image: ImageBitmap?,
    val description: String,
    val selectedGenreIndex: Int,
    val genres: List<String>,
    val rating: Int,
    val date: Long,
    val plannedMode: Boolean,
    val isFavorite: Boolean
)

