package com.elena_balakhnina.bookdiary.viewelement

import androidx.compose.ui.graphics.ImageBitmap

data class ViewElementData(
    val bookTitle: String = "",
    val author: String = "",
    val description: String = "",
    val date: Long = -1,
    val rating: Int = -1,
    val genre: String = "",
    val image: ImageBitmap? = null,
    val allowRate: Boolean = false,
)