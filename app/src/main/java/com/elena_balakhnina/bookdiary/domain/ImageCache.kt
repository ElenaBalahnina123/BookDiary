package com.elena_balakhnina.bookdiary.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.inject.Inject


@Module
@InstallIn(SingletonComponent::class)
abstract class ImageCacheModule {

    @Binds
    abstract fun bindCache(cacheImpl: ImageCacheImpl): ImageCache
}



interface ImageCache {

    suspend fun saveImageFromUri(uri: Uri): String?

    suspend fun saveImageFromBitmap(bitmap: Bitmap): String

    suspend fun saveImageSteamToCache(inputStream: InputStream): String

    suspend fun getBitmapFromCache(uuid: String?): ImageBitmap?

    suspend fun deleteImage(uuid: String)

}

class ImageCacheImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : ImageCache {

    private val inMemoryCache = LruCache<String,Bitmap>(100)

    override suspend fun saveImageFromUri(uri: Uri): String? {
        return Contexts.getApplication(context).contentResolver.openInputStream(uri)?.use {
            saveImageSteamToCache(it)
        }
    }

    override suspend fun saveImageFromBitmap(bitmap: Bitmap): String =
        withContext(Dispatchers.IO) {
            val fileName = UUID.randomUUID().toString()
            val file = File(context.cacheDir, fileName)
            if (!file.createNewFile()) throw IOException("unable to create file")
            file.outputStream().use { fileOutputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG,100, fileOutputStream)
            }
            inMemoryCache.put(fileName,bitmap)
            fileName
        }

    override suspend fun saveImageSteamToCache(inputStream: InputStream): String =
        withContext(Dispatchers.IO) {

            val fileName = UUID.randomUUID().toString()
            val file = File(context.cacheDir, fileName)

            if (!file.createNewFile()) throw IOException("unable to create file")
            file.outputStream().use { fileOutputStream ->
                inputStream.copyTo(fileOutputStream)
            }
            fileName
        }

    override suspend fun getBitmapFromCache(uuid: String?): ImageBitmap? =
        withContext(Dispatchers.IO) {
            if (uuid.isNullOrBlank()) return@withContext null

            val cached = inMemoryCache.get(uuid)
            if(cached != null) return@withContext cached

            val file = File(context.cacheDir, uuid)

            if (file.exists()) {
                file.inputStream().use {
                    BitmapFactory.decodeStream(it)?.also {
                        inMemoryCache.put(uuid,it)
                    }
                }
            } else null
        }?.asImageBitmap()

    override suspend fun deleteImage(uuid: String) {
        val file = File(context.cacheDir, uuid)
        if(file.exists()) {
            inMemoryCache.remove(uuid)
            file.delete()
        }
    }

}