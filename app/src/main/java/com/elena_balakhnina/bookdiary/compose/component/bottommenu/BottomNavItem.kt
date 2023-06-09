package com.elena_balakhnina.bookdiary.compose.component.bottommenu

import com.elena_balakhnina.bookdiary.R

sealed class BottomNavItem(var title: String, var icon: Int, var screen_route: String) {
    object Books : BottomNavItem("Мои книги", R.drawable.my_books, "books")
    object Planned : BottomNavItem("Планы", R.drawable.time_and_calendar, "planned")
    object Favorite : BottomNavItem("Избранное", R.drawable.favorite, "favorite")
}