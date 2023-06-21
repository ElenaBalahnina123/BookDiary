package com.elena_balakhnina.bookdiary.editor

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue

data class EditElementData(
    val bookTitle: TextFieldValue,
    val author: TextFieldValue,
    val image: ImageBitmap?,
    val description: TextFieldValue,
    val selectedGenreIndex: Int,
    val genres: List<String>,
    val rating: Int,
    val date: Long,
    val plannedMode: Boolean,
    val isFavorite : Boolean
)