package com.elena_balakhnina.bookdiary.genreSyncWork

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.elena_balakhnina.bookdiary.domain.GenresRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GenreSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val genresRepository: GenresRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        genresRepository.fetchRemoteGenres()
        return Result.success()
    }

    companion object {
        fun start(context: Context): Operation {
            // Create the Constraints
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // Bring it all together by creating the WorkRequest; this also sets the back off criteria
            val workRequest = OneTimeWorkRequestBuilder<GenreSyncWorker>()
//                .setInputData(inputData)
                .setConstraints(constraints)
                .build()

            // Enqueue work request
            return WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}