package com.hsact.data.sync.worker

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncAllShiftsWorker(
    context: Context,
    workerParams: WorkerParameters,
) : BaseFirebaseWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        return@withContext try {
            shiftSyncManager.sync()
            Log.d("SyncAllShiftsWorker", "All shifts synced")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncAllShiftsWorker", "Error syncing shifts", e)
            Result.retry()
        }
    }
}