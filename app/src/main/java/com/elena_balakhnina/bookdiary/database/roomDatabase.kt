package com.elena_balakhnina.bookdiary.database

import android.content.Context
import androidx.room.*
import androidx.startup.AppInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Dao
interface GenreDao {
    @Query("SELECT COUNT(fb_id) FROM genres")
    fun countGenresSync(): Long

    @Query("SELECT COUNT(fb_id) FROM genres")
    suspend fun countGenres(): Long

    @Query("SELECT * FROM genres")
    suspend fun getAllGenres(): List<GenreDBEntity>

    @Query("DELETE FROM genres")
    suspend fun deleteAllGenres()

    @Insert
    fun insertGenresSync(list: List<GenreDBEntity>)

    @Insert
    suspend fun insertGenres(list: List<GenreDBEntity>)

    @Query("SELECT * FROM genres WHERE fb_id = :id LIMIT 1")
    suspend fun getByIdGenre(id: String): GenreDBEntity?
}


@Dao
interface BooksDao {
    @Query("SELECT * FROM books")
    fun getAllBooksFlow(): Flow<List<BookDbEntity>>

    @Query("SELECT * FROM books WHERE rating > 0 AND (bookTitle LIKE '%' || :theQuery || '%' OR author LIKE '%' || :theQuery || '%') ORDER BY id DESC")
    fun getBooksByQuery(theQuery: String): Flow<List<BookDbEntity>>

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

    @Query("SELECT count(id) FROM books")
    suspend fun countAll(): Long

    @Query("SELECT * FROM books WHERE rating <= 0 ORDER BY id DESC")
    fun getPlannedBooks(): Flow<List<BookDbEntity>>

    @Query("SELECT * FROM books WHERE rating > 0 ORDER BY id DESC")
    fun getRatedBooks(): Flow<List<BookDbEntity>>

    @Query("SELECT * FROM books WHERE isFavorite ORDER BY id DESC")
    fun getFavoriteBooks(): Flow<List<BookDbEntity>>

}

@Database(entities = [BookDbEntity::class, GenreDBEntity::class], version = 13)
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
        return AppInitializer.getInstance(context).initializeComponent(AppDbInitializer::class.java)
        /*return Room.databaseBuilder(context, AppDatabase::class.java, "books_diary")
            .fallbackToDestructiveMigration()
            .build()*/
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
