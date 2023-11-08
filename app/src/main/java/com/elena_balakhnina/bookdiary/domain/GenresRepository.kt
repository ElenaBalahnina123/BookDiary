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