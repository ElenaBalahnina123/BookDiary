package com.elena_balakhnina.bookdiary.editor

import androidx.compose.ui.text.input.TextFieldValue
import com.elena_balakhnina.bookdiary.domain.Genre

data class EditElementVmState(
    val bookTitle: TextFieldValue = TextFieldValue(),
    val author: TextFieldValue = TextFieldValue(),
    val description: TextFieldValue = TextFieldValue(),
    val date: Long = System.currentTimeMillis(),
    val rating: Int = -1,
    val image: String? = null,
    val plannedMode: Boolean = false,
    val isFavorite: Boolean = false,
    val genres: List<Genre> = emptyList(),
    val selectedGenreIndex: Int = -1,
)