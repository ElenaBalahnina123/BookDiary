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
import kotlinx.coroutines.withContext
import java.util.LinkedList
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

    suspend fun fetchRemoteGenres()

//    suspend fun awaitInit()

    suspend fun getAllGenres(): List<Genre>

    suspend fun getById(generId: String): Genre?
}

private typealias DbGenre = GenreDBEntity

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

    override suspend fun fetchRemoteGenres(): Unit = withContext(Dispatchers.IO) {
        val lastUpdated = appPreferences.lastUpdateDate()
        if(System.currentTimeMillis() - lastUpdated < MIN_UPDATE_INTERVAL) return@withContext
        Log.d("GenresRepository","fetching remote genres...")
        kotlin.runCatching {
            loadGenresFromFb().associate { it.fbId to it.genre }
        }.mapCatching { fbGenresMap ->
            val dbGenresMap = genresDao.getAllGenres().associate { it.fbId to it.genre }

            val addedGenres = LinkedList<DbGenre>()

            fbGenresMap.forEach { (fbId, genre) ->
                if(!dbGenresMap.containsKey(fbId)) {
                    addedGenres.add(DbGenre(fbId, genre))
                }
            }

            val idsForDelete = dbGenresMap.keys.filter {
                it !in fbGenresMap.keys
            }

            Log.d("GenresRepository", "fetched ${fbGenresMap.size} genres, new ${addedGenres.size}, removed ${idsForDelete.size}")

            if(addedGenres.isNotEmpty()) {
                genresDao.insertGenres(addedGenres)
            }
            if(idsForDelete.isNotEmpty()) {
                genresDao.deleteById(idsForDelete)
            }
        }.onSuccess {
            appPreferences.updateLastUpdateDate()
        }.onFailure {
            Log.e("GenresRepository","unable to fetch genres")
        }
    }

    private suspend fun loadGenresFromFb(): List<DbGenre> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { cont ->
                Firebase.firestore.collection("genres")
                    .orderBy("genre")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val genres = querySnapshot.map {
                            GenreDBEntity(
                                fbId = it.id,
                                genre = it["genre"].toString()
                            )
                        }
                        cont.resume(genres)
                    }
                    .addOnFailureListener {
                        cont.resumeWithException(it)
                    }
            }
        }
    }

    override suspend fun getAllGenres(): List<Genre> {
        return genresDao.getAllGenres()
    }

    override suspend fun getById(generId: String): Genre? {
        return genresDao.getByIdGenre(generId)
    }
}