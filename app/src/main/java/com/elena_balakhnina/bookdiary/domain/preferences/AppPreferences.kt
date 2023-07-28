package com.elena_balakhnina.bookdiary.domain.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    @Singleton
    abstract fun bind(impl: PreferencesImpl): AppPreferences
}

interface AppPreferences {

    suspend fun lastUpdateDate(): Long

    suspend fun updateLastUpdateDate()
}


class PreferencesImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : AppPreferences {

    private val LAST_UPDATE = longPreferencesKey("last_update")

    override suspend fun lastUpdateDate(): Long {
        return context.dataStore.data.map {
            it[LAST_UPDATE] ?: 0L
        }.first()
    }

    override suspend fun updateLastUpdateDate() {
        context.dataStore.edit {
            it[LAST_UPDATE] = System.currentTimeMillis()
        }
    }
}