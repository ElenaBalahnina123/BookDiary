package com.elena_balakhnina.bookdiary.editor

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import com.elena_balakhnina.bookdiary.compose.component.DropDownState

data class EditElementData(
    val bookTitle: TextFieldValue,
    val author: TextFieldValue,
    val description: TextFieldValue,
    val image: ImageBitmap?,
    val genresDropDownState: DropDownState,
//    val selectedGenreIndex: Int,
//    val genres: List<String>,
//    val rating: Int,
    val ratingState: DropDownState,
    val date: Long,
    val plannedMode: Boolean,
    val isFavorite: Boolean
)

