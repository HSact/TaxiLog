package com.hsact.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hsact.data.firebase.FirebaseShiftDataSource
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteShiftWorker (
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface FirebaseShiftEntryPoint {
        fun firebaseShiftDataSource(): FirebaseShiftDataSource
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val remoteId = inputData.getString("remoteId") ?: return@withContext Result.failure()

        val firebaseShiftDataSource = EntryPointAccessors.fromApplication(
            applicationContext,
            FirebaseShiftEntryPoint::class.java
        ).firebaseShiftDataSource()

        try {
            firebaseShiftDataSource.delete(remoteId)
            Log.d("DeleteShiftWorker", "Shift with remoteId $remoteId deleted from Firebase")
            Result.success()
        } catch (e: Exception) {
            Log.e("DeleteShiftWorker", "Error deleting shift from Firebase", e)
            Result.retry()
        }
    }
}