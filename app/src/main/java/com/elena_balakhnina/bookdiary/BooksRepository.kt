package com.elena_balakhnina.bookdiary

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
)

//data class GenresEntity(
//    val id : Long,
//    val genre: String
//)

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

    //Получить последовательный список книг
    fun booksFlow(): Flow<List<BookEntity>>

    fun plannedBooksFlow(): Flow<List<BookEntity>>

    fun ratedBooksFlow(): Flow<List<BookEntity>>

    //Получить одну прочитанную книгу по bookId
    fun bookEntityFlow(bookId: Long): Flow<BookEntity>
}

class BooksRepositoryImpl @Inject constructor(
    private val booksDao: BooksDao,
    private val genresDao: GenreDao,
) : BooksRepository {

    override fun plannedBooksFlow(): Flow<List<BookEntity>> {
        return flow {
            val genres: Map<Long, GenreDBEntity> = genresDao.getAllGenres().associateBy { it.id }
            booksDao.getPlannedBooks().map {
                it.map {
                    BookEntity(
                        id = it.id,
                        bookTitle = it.bookTitle,
                        author = it.author,
                        description = it.description,
                        date = it.date,
                        rating = it.rating,
                        image = it.image,
                        genre = genres[it.genreId]!!
                    )
                }
            }.collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
        /*return booksDao.getPlannedBooks().map {
            it.map { it.toBookEntity() }
        }.flowOn(Dispatchers.IO)*/
    }

    override fun ratedBooksFlow(): Flow<List<BookEntity>> {
        return flow {
            val genres: Map<Long, GenreDBEntity> = genresDao.getAllGenres().associateBy { it.id }
            booksDao.getRatedBooks().map {
                it.map {
                    BookEntity(
                        id = it.id,
                        bookTitle = it.bookTitle,
                        author = it.author,
                        description = it.description,
                        date = it.date,
                        rating = it.rating,
                        image = it.image,
                        genre = genres[it.genreId]!!
                    )
                }
            }.collect {
                emit(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    //Метод расширения для BookEntity, который в итоге будет возвращать BookDbEntity
    private fun BookEntity.toDbEntity(): BookDbEntity {
        return BookDbEntity(
            id = id,
            bookTitle = bookTitle,
            author = author,
            description = description,
            date = date,
            rating = rating,
            image = image,
            genreId = genre.id,
        )
    }

    //Метод расширения для BookDbEntity, который в итоге будет возвращать BookEntity
    private suspend fun BookDbEntity.toBookEntity(): BookEntity {
        return BookEntity(
            id = id,
            bookTitle = bookTitle,
            author = author,
            description = description,
            date = date,
            rating = rating,
            image = image,
            genre = genresDao.getByIdGenre(genreId)?.toGenreEntity()!!
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
//
//    override suspend fun getAllGenres(): Flow<List<Genre>> {
//        return booksDao.getAllGenresFlow().map {
//            it.map {
//                it.toGenreEntity()
//            }
//        }
//    }
//
//    override suspend fun insertGenres(addGenre: List<Genre>) {
//
//    }


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

    override fun booksFlow(): Flow<List<BookEntity>> {
        return booksDao.getAllBooksFlow().map { list ->
            list.map { it.toBookEntity()}
        }
    }

}