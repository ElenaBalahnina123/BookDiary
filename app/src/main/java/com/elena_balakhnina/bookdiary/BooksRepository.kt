package com.elena_balakhnina.bookdiary

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


data class BookEntity(
    val id: Long?,
    val bookTitle: String,
    val author: String,
    val description: String?,
    val date: String,
    val rating: String,
    val genre: String,
    val image: String?,
)

interface BooksRepository {

    suspend fun save(book: BookEntity)

    suspend fun getById(bookId: Long): BookEntity?

    suspend fun getAll(): List<BookEntity>

    suspend fun delete(bookId: Long)

    fun booksFlow(): Flow<List<BookEntity>>
}

class BooksRepositoryImpl @Inject constructor(
    private val booksDao: BooksDao,
) : BooksRepository {

    private fun BookEntity.toDbEntity(): BookDbEntity {
        return BookDbEntity(
            id = requireNotNull(id),
            bookTitleDB = bookTitle,
            authorDB = author,
            descriptionDB = description,
            dateDB = date,
            ratingDB = rating,
            genreDB = genre,
            imageDB = image
        )
    }

    private fun BookDbEntity.toBookEntity(): BookEntity {
        return BookEntity(
            id = id,
            bookTitle = bookTitleDB,
            author = authorDB,
            description = descriptionDB,
            date = dateDB,
            rating = ratingDB,
            genre = genreDB,
            image = imageDB
        )
    }

    override suspend fun save(book: BookEntity) {
        if (book.id != null && book.id > 0) {
            booksDao.updateBook(book.toDbEntity())
        } else {
            booksDao.insertBook(book.toDbEntity())
        }
    }

    override suspend fun getById(bookId: Long): BookEntity? {
        return booksDao.getById(bookId)?.toBookEntity()
    }

    override suspend fun getAll(): List<BookEntity> {
        return booksDao.getAllBooks()
            .map { it.toBookEntity() }
    }

    override suspend fun delete(bookId: Long) {
        booksDao.delete(bookId)
    }

    override fun booksFlow(): Flow<List<BookEntity>> {
        return booksDao.getAllBooksFlow().map { list ->
            list.map {
                it.toBookEntity()
            }
        }
    }

}