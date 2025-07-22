package com.hsact.data.sync.worker

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteShiftWorker(
    context: Context,
    workerParams: WorkerParameters,
) : BaseFirebaseWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val remoteId = inputData.getString("remoteId") ?: return@withContext Result.failure()

        return@withContext try {
            firebaseShiftDataSource.delete(remoteId)
            Log.d("DeleteShiftWorker", "Deleted shift with remoteId $remoteId")
            Result.success()
        } catch (e: Exception) {
            Log.e("DeleteShiftWorker", "Error deleting shift", e)
            Result.retry()
        }
    }
}