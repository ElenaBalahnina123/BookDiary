package com.elena_balakhnina.bookdiary

import android.content.Context
import androidx.room.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

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
    @ColumnInfo(name = "genre") val genreId: Long,
    @ColumnInfo(name = "image") val image: String?,
)

@Entity(tableName = "genres")
data class GenreDBEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "genre") val genre: String,
)

@Dao
interface GenreDao {
    @Query("SELECT COUNT(id) FROM genres")
    suspend fun countGenres(): Long

    @Query("SELECT * FROM genres")
    suspend fun getAllGenres(): List<GenreDBEntity>

    @Insert
    suspend fun insertGenres(list: List<GenreDBEntity>)

    @Query("SELECT * FROM genres WHERE id = :id LIMIT 1")
    suspend fun getByIdGenre(id: Long): GenreDBEntity?
}



@Dao
interface BooksDao {
    @Query("SELECT * FROM books")
    fun getAllBooksFlow(): Flow<List<BookDbEntity>>

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    suspend fun getById(bookId: Long): BookDbEntity?

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    fun subscribeById(bookId: Long): Flow<BookDbEntity?>

    @Insert
    suspend fun insertBook(book: BookDbEntity)

    @Update
    suspend fun updateBook(book: BookDbEntity)

    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun delete(bookId: Long)

    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<BookDbEntity>

    @Query("SELECT count(id) FROM books")
    suspend fun countAll(): Long

    @Query("SELECT * FROM books WHERE rating <= 0")
    fun getPlannedBooks(): Flow<List<BookDbEntity>>

    @Query("SELECT * FROM books WHERE rating > 0")
    fun getRatedBooks(): Flow<List<BookDbEntity>>

}

@Database(entities = [BookDbEntity::class, GenreDBEntity::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BooksDao

    abstract fun genreDao(): GenreDao
}

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDb(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "books_diary")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideBooksDao(db: AppDatabase): BooksDao {
        return db.bookDao()
    }

    @Provides
    fun provideGenreDao(db: AppDatabase): GenreDao {
        return db.genreDao()
    }
}
