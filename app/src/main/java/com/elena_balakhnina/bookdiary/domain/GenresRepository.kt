package com.elena_balakhnina.bookdiary.domain

import android.util.Log
import com.elena_balakhnina.bookdiary.database.GenreDBEntity
import com.elena_balakhnina.bookdiary.database.GenreDao
import com.elena_balakhnina.bookdiary.domain.preferences.AppPreferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Module
@InstallIn(SingletonComponent::class)
abstract class GenresRepoModule {

    @Binds
    abstract fun bindGenresRepo(impl: GenresRepositoryImpl): GenresRepository
}

interface GenresRepository {

    suspend fun awaitInit()

    suspend fun getAllGenres(): List<Genre>

    suspend fun getById(generId: String): Genre?
}

fun getDefaultGenres() = mapOf<String, String>(
    "1BWhTi136dqD8fIQyY56" to "Приключения",
    "1MRhhExOlq1y3E4blnKr" to "Мистика",
    "1Okohhrax4Amh9oSx4YP" to "Фанфик",
    "4ztwSG4Fuaudz9DCrMT3" to "Вестерн",
    "59jmvfu2Wb4pyeMbAZ5q" to "Юмор",
    "7tKyZdOl0ZXU0eM5S8is" to "Художественная литература",
    "8tdOLmMjSkrBGOT6XONt" to "Наука",
    "A762F8e33rXRDPw92Dyd" to "Психология",
    "ByqPKAsYRGX3l2O9qZlf" to "Биография",
    "DTAHWNKNDyRgaJTS5Obq" to "Научная фантастика",
    "EGkhBvf9Sn1FGGgLjzzw" to "Боевик",
    "EoHoR2JsGszvJZH7XrPP" to "Повесть",
    "FjNrFiV5J2ZclFLbtOjk" to "Техника",
    "GENRE_NOT_SET" to "Не указан",
    "ItYMC9akFR7q1HnmOMTG" to "Авангардная литература",
    "JCHnbZf0pigz9BwdSNad" to "Комикс, манга",
    "JnRmznnnpe2YzOG5xWPD" to "Любовный роман",
    "O4mCK3ItaRPg60huMYta" to "Сказка",
    "O8CZKN1FR7IA0dHJ6778" to "Поэзия",
    "Ojt7yXJC7hZcEk3zCATe" to "Воспитание",
    "QqrHmUfMYsV7YV5KNZ9e" to "Искусство",
    "R5I7hCX5nTfrOzeJLUBf" to "Триллер",
    "UQ77wr0iRm1d7g5YDRtv" to "Бизнес",
    "UWlsCY9bSiwLWHbcObtu" to "Классика",
    "VGjNpoj72WHP3RyiaYmN" to "Детская литература",
    "W6LdkU9R3dbLPK1dfSpF" to "18+",
    "YkPCR44CBpQYxjGnhByl" to "Питание и кулинария",
    "ZmOZkGL6GaChpoefvHaR" to "Фантастика",
    "b2R2iQNptbh9Kyh9o08w" to "Политика, экономика и право",
    "bCj4KTiWmXid59aQp4vS" to "Журнал, газета",
    "dXnd8Uf0aCijJOXxayTk" to "Young Adult",
    "dvCaw0okFv1VAJ4ABw9b" to "Мифы и легенды",
    "hPi7bYiKZhabDxIIOUNf" to "Энциклопедия",
    "hjotzpjwAQgdych035XP" to "Философия",
    "lx5NGbQz5J5RX7V2tzfN" to "Мода и красота",
    "mNgzTQaQhauamaCsqkoi" to "Роман",
    "pHHozQs9tj2uTdMnNUhE" to "Современная литература",
    "qYJEswtfLR5LzBJ9DUKG" to "Ужасы",
    "rWq9EoyXg8jgiFsZRdLF" to "Учебная литература",
    "tC53iNZ1HUArs7SuqSLp" to "Исторический роман",
    "tYqDn7kJEkP8fHFGdN7t" to "Детектив",
    "zPUR2cFzis19MFtIgUEm" to "Здоровье",
    "zwFrZo27PfIJhMRRI3fo" to "Фэнтези",

)



@Singleton
class GenresRepositoryImpl @Inject constructor(
    private val genresDao: GenreDao,
    private val appPreferences: AppPreferences,
) : GenresRepository {

    companion object {
        // day: 86400000
        // minute:  60000
        private const val MIN_UPDATE_INTERVAL = 86400000
    }

    private val initJob = GlobalScope.launch(Dispatchers.IO) {
        val savedCount = genresDao.countGenres()
        Log.d("GENRES", "count: $savedCount")

        val lastUpdated = appPreferences.lastUpdateDate()

        if (savedCount == 0L || (System.currentTimeMillis() - lastUpdated > MIN_UPDATE_INTERVAL)) {
            kotlin.runCatching {
                suspendCoroutine { cont ->
                    Firebase.firestore.collection("genres")
                        .orderBy("genre")
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val genres = querySnapshot.map {
                                GenreDBEntity(
                                    fbId = it.id,
                                    genre = it.get("genre").toString()
                                )
                            }
                            cont.resume(genres)
                        }
                        .addOnFailureListener {
                            cont.resumeWithException(it)
                        }
                }
            }.onSuccess { genres ->
                genresDao.deleteAllGenres()
                genresDao.insertGenres(genres)
                appPreferences.updateLastUpdateDate()
            }
        }
    }

    override suspend fun awaitInit() {
        initJob.join()
    }

    override suspend fun getAllGenres(): List<Genre> {
        initJob.join()
        return genresDao.getAllGenres()
    }

    override suspend fun getById(generId: String): Genre? {
        initJob.join()
        return genresDao.getByIdGenre(generId)
    }
}