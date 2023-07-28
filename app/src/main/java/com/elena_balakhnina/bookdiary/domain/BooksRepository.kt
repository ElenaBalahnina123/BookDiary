package com.elena_balakhnina.bookdiary.domain

import android.util.Log
import com.elena_balakhnina.bookdiary.database.BookDbEntity
import com.elena_balakhnina.bookdiary.database.BooksDao
import com.elena_balakhnina.bookdiary.database.GenreDBEntity
import com.elena_balakhnina.bookdiary.database.GenreDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

typealias Genre = GenreDBEntity

data class BookEntity(
    val id: Long?,
    val bookTitle: String,
    val author: String,
    val description: String?,
    val date: Long,
    val rating: Int,
    val genre: Genre,
    val image: String?,
    val plannedMode: Boolean,
    val isFavorite: Boolean
)

@Module
@InstallIn(SingletonComponent::class)
abstract class BookRepositoryModule {
    @Binds
    abstract fun bindBooksRepository(impl: BooksRepositoryImpl): BooksRepository
}

interface BooksRepository {

    suspend fun save(book: BookEntity)
    suspend fun getById(bookId: Long): BookEntity?

    suspend fun getAll(): List<BookEntity>
    suspend fun delete(bookId: Long)

    fun plannedBooksFlow(): Flow<List<BookEntity>>

    fun favoriteBooksFlow(): Flow<List<BookEntity>>

    fun ratedBooksFlow(): Flow<List<BookEntity>>

    fun bookEntityFlow(bookId: Long): Flow<BookEntity>

    suspend fun setFavorite(bookId: Long, isFavorite: Boolean)
}

class BooksRepositoryImpl @Inject constructor(
    private val booksDao: BooksDao,
    private val genresDao: GenreDao,
) : BooksRepository {

    override suspend fun setFavorite(bookId: Long, isFavorite: Boolean) {
        booksDao.getById(bookId)?.copy(
            isFavorite = isFavorite
        )?.let {
            booksDao.updateBook(it)
            Log.d("OLOLO", "book with $bookId set favorite to $isFavorite")
        }
    }

    override fun plannedBooksFlow(): Flow<List<BookEntity>> {
        return flow {
            val genres: Map<String, GenreDBEntity> =
                genresDao.getAllGenres().associateBy { it.fbId }
            booksDao.getPlannedBooks().map { bookDbEntities ->
                bookDbEntities.map {
                    BookEntity(
                        id = it.id,
                        bookTitle = it.bookTitle,
                        author = it.author,
                        description = it.description,
                        date = it.date,
                        rating = it.rating,
                        image = it.image,
                        genre = genres[it.genreId]!!,
                        plannedMode = it.showRateAndDate,
                        isFavorite = it.isFavorite
                    )
                }
            }.collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun favoriteBooksFlow(): Flow<List<BookEntity>> {
        return flow {
            val genres: Map<String, GenreDBEntity> =
                genresDao.getAllGenres().associateBy { it.fbId }
            booksDao.getFavoriteBooks().map {
                it.map {
                    BookEntity(
                        id = it.id,
                        bookTitle = it.bookTitle,
                        author = it.author,
                        description = it.description,
                        date = it.date,
                        rating = it.rating,
                        image = it.image,
                        genre = genres[it.genreId]!!,
                        plannedMode = it.showRateAndDate,
                        isFavorite = it.isFavorite
                    )
                }
            }.collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun ratedBooksFlow(): Flow<List<BookEntity>> {
        return flow {
            val genres: Map<String, GenreDBEntity> =
                genresDao.getAllGenres().associateBy { it.fbId }
            booksDao.getRatedBooks().map { bookDbEntities ->
                bookDbEntities.map {

                    val genre = requireNotNull(genres[it.genreId]) {
                        "genre with id ${it.genreId} not found! (total genres count: ${genres.size})"
                    }

                    BookEntity(
                        id = it.id,
                        bookTitle = it.bookTitle,
                        author = it.author,
                        description = it.description,
                        date = it.date,
                        rating = it.rating,
                        image = it.image,
                        genre = genre,
                        plannedMode = it.showRateAndDate,
                        isFavorite = it.isFavorite
                    )
                }
            }.collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun BookEntity.toDbEntity(): BookDbEntity {
        return BookDbEntity(
            id = id,
            bookTitle = bookTitle,
            author = author,
            description = description,
            date = date,
            rating = rating,
            image = image,
            genreId = genre.fbId,
            showRateAndDate = plannedMode,
            isFavorite = isFavorite
        )
    }

    private suspend fun BookDbEntity.toBookEntity(): BookEntity {
        return BookEntity(
            id = id,
            bookTitle = bookTitle,
            author = author,
            description = description,
            date = date,
            rating = rating,
            image = image,
            genre = genresDao.getByIdGenre(genreId)?.toGenreEntity()!!,
            plannedMode = showRateAndDate,
            isFavorite = isFavorite
        )
    }

    private fun GenreDBEntity.toGenreEntity(): Genre {
        return this
    }

    override fun bookEntityFlow(bookId: Long): Flow<BookEntity> {
        return booksDao.subscribeById(bookId).mapNotNull {
            it?.toBookEntity()
        }
    }

    override suspend fun save(book: BookEntity) {
        val dbBook = book.toDbEntity()

        if (book.id != null && book.id > 0) {
            booksDao.updateBook(dbBook)
        } else {
            booksDao.insertBook(dbBook)
        }
    }

    override suspend fun getById(bookId: Long): BookEntity? {
        return booksDao.getById(bookId)?.toBookEntity()
    }

    override suspend fun getAll(): List<BookEntity> {
        return booksDao.getAllBooks().map { it.toBookEntity() }
    }

    override suspend fun delete(bookId: Long) {
        booksDao.delete(bookId)
    }
}