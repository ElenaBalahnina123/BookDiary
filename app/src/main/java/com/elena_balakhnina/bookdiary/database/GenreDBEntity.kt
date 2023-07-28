package com.elena_balakhnina.bookdiary.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class GenreDBEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "fb_id") val fbId: String,
    @ColumnInfo(name = "genre") val genre: String,
)