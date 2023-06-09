package com.elena_balakhnina.bookdiary

import android.util.Log
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GenresRepoModule {

    @Binds
    abstract fun bindGenresRepo(impl: GenersRepositoryImpl): GenresRepository
}

interface GenresRepository {

    suspend fun getAllGenres(): List<Genre>

    suspend fun getById(generId: Long): Genre?
}

@Singleton
class GenersRepositoryImpl @Inject constructor(
    private val genresDao: GenreDao
) : GenresRepository {

    private val initJob = GlobalScope.launch(Dispatchers.IO) {
        val count = genresDao.countGenres()
        Log.d("GENRES","count: $count")
        if(count == 0L) {
            setOf(
                "Авангардная литература",
                "Бизнес",
                "Биография",
                "Боевик",
                "Вестерн",
                "Воспитание",
                "Детектив",
                "Детская литература",
                "Журнал, газета",
                "Здоровье",
                "Искусство",
                "Исторический роман",
                "Комикс, манга",
                "Классика",
                "Любовный роман",
                "Мистика",
                "Мифы и легенды",
                "Мода и красота",
                "Наука",
                "Научная фантастика",
                "Питание и кулинария",
                "Повесть",
                "Политика, экономика и право",
                "Поэзия",
                "Приключения",
                "Психология",
                "Роман",
                "Сказка",
                "Современная литература",
                "Техника",
                "Триллер",
                "Ужасы",
                "Учебная литература",
                "Фантастика",
                "Философия",
                "Фэнтези",
                "Энциклопедия",
                "Юмор",
                "18+",
            ).map {
                GenreDBEntity(
                    id = 0L,
                    genre = it
                )
            }.let {
                Log.d("GENRES","inserting genres...")
                genresDao.insertGenres(it)
                Log.d("GENRES","count: ${genresDao.countGenres()}")
            }
        }
    }

    override suspend fun getAllGenres(): List<Genre> {
        initJob.join()
        return genresDao.getAllGenres()
    }

    override suspend fun getById(generId: Long): Genre? {
        initJob.join()
        return genresDao.getByIdGenre(generId)
    }
}