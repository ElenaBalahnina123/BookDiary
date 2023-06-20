package com.elena_balakhnina.bookdiary.booklist

import com.elena_balakhnina.bookdiary.BookItemData
import com.elena_balakhnina.bookdiary.ViewElementScreenData


data class BookListVmState(
    val books: List<BookItemData> = emptyList()
)



