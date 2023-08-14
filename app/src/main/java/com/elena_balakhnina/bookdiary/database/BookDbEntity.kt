package com.elena_balakhnina.bookdiary.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "books"
)
data class BookDbEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long?,
    @ColumnInfo(name = "bookTitle") val bookTitle: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "genre") val genreId: String,
    @ColumnInfo(name = "image") val image: String?,
    @ColumnInfo(name = "showRateAndDate") val showRateAndDate: Boolean,
    @ColumnInfo(name = "isFavorite") val isFavorite: Boolean
)