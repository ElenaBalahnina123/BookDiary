package com.elena_balakhnina.bookdiary

import android.content.Context
import androidx.room.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


private const val DATA_BASE_NAME = "books_diary"
private const val TABLE_NAME = "books_table"
private const val ID_DB = "idDB"
private const val BOOK_TITLE_DB = "bookTitleDB"
private const val AUTHOR_DB = "authorDB"
private const val DESCRIPTION_DB = "descriptionDB"
private const val DATE_DB = "dateDB"
private const val RATING_DB = "ratingDB"
private const val GENRE_DB = "genreDB"
private const val IMAGE_DB = "imageDB"

@Entity(
    tableName = TABLE_NAME
)
data class BookDbEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID_DB) val id: Long,
    @ColumnInfo(name = BOOK_TITLE_DB) val bookTitleDB: String,
    @ColumnInfo(name = AUTHOR_DB) val authorDB: String,
    @ColumnInfo(name = DESCRIPTION_DB) val descriptionDB: String?,
    @ColumnInfo(name = DATE_DB) val dateDB: String,
    @ColumnInfo(name = RATING_DB) val ratingDB: String,
    @ColumnInfo(name = GENRE_DB) val genreDB: String,
    @ColumnInfo(name = IMAGE_DB) val imageDB: String?,
)

@Dao
interface BooksDao {
    @Query("SELECT * FROM $TABLE_NAME")
    fun getAllBooksFlow(): Flow<List<BookDbEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE $ID_DB = :bookId LIMIT 1")
    suspend fun getById(bookId: Long): BookDbEntity?

    @Insert
    suspend fun insertBook(book: BookDbEntity)

    @Update
    suspend fun updateBook(book: BookDbEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE $ID_DB = :bookId")
    suspend fun delete(bookId: Long)

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getAllBooks(): List<BookDbEntity>
}

@Database(entities = [BookDbEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BooksDao
}

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideBooksDao(@ApplicationContext appContext: Context): BooksDao {
        return Room.databaseBuilder(appContext, AppDatabase::class.java, DATA_BASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
            .bookDao()
    }
}
