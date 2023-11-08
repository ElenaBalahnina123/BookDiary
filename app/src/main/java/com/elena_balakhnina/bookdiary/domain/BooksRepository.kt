package com.elena_balakhnina.bookdiary.domain

import com.elena_balakhnina.bookdiary.database.BookDbEntity
import com.elena_balakhnina.bookdiary.database.BooksDao
import com.elena_balakhnina.bookdiary.database.GenreDBEntity
import com.elena_balakhnina.bookdiary.database.GenreDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    suspend fun delete(bookId: Long)

    fun plannedBooksFlow(): Flow<List<BookEntity>>

    fun favoriteBooksFlow(): Flow<List<BookEntity>>

    fun bookEntityFlow(bookId: Long): Flow<BookEntity>

    suspend fun setFavorite(bookId: Long, isFavorite: Boolean)

    fun getRatedBooksWithQuery(query: String): Flow<List<BookEntity>>
}

class BooksRepositoryImpl @Inject constructor(
    private val booksDao: BooksDao,
    private val genresDao: GenreDao,
) : BooksRepository {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val genresTask = scope.async {
        genresDao.getAllGenres().associateBy { it.fbId }
    }

    override fun getRatedBooksWithQuery(query: String): Flow<List<BookEntity>> {
        return booksDao.getBooksByQuery(query)
            .mapToBookEntity()
    }

    override fun plannedBooksFlow(): Flow<List<BookEntity>> {
        return booksDao.getPlannedBooks()
            .mapToBookEntity()
    }

    override fun favoriteBooksFlow(): Flow<List<BookEntity>> {
        return booksDao.getFavoriteBooks()
            .mapToBookEntity()
    }

    override suspend fun setFavorite(bookId: Long, isFavorite: Boolean) {
        booksDao.getById(bookId)?.copy(
            isFavorite = isFavorite
        )?.let {
            booksDao.updateBook(it)
        }
    }

    override fun bookEntityFlow(bookId: Long): Flow<BookEntity> {
        return flow {
            val genres = genresTask.await()
            booksDao.subscribeById(bookId).mapNotNull {
                it?.toBookEntity(genres)
            }.collect {
                emit(it)
            }
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
        return booksDao.getById(bookId)?.toBookEntity(genresTask.await())
    }

    override suspend fun delete(bookId: Long) {
        booksDao.delete(bookId)
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

    private suspend fun mapToBookEntity(
        list: List<BookDbEntity>,
    ): List<BookEntity> {
        val genres = genresTask.await()
        return list.map { it.toBookEntity(genres) }
    }

    private fun BookDbEntity.toBookEntity(
        genres: Map<String, GenreDBEntity>
    ): BookEntity {
        return BookEntity(
            id = id,
            bookTitle = bookTitle,
            author = author,
            description = description,
            date = date,
            rating = rating,
            image = image,
            genre = genres[genreId]?.toGenreEntity()!!,
            plannedMode = showRateAndDate,
            isFavorite = isFavorite
        )
    }

    private fun GenreDBEntity.toGenreEntity(): Genre {
        return this
    }

    private fun Flow<List<BookDbEntity>>.mapToBookEntity(): Flow<List<BookEntity>> {
        return flow {
            val genres = genresTask.await()
            collect {
                val mapped = it.map { it.toBookEntity(genres) }
                emit(mapped)
            }
        }.flowOn(Dispatchers.IO)
    }
}