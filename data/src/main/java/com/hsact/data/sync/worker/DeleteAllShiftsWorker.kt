package com.hsact.data.sync.worker

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAllShiftsWorker(
    context: Context,
    workerParams: WorkerParameters,
) : BaseFirebaseWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        return@withContext try {
            firebaseShiftDataSource.deleteAll()
            Log.d("DeleteShiftWorker", "Deleted all shifts")
            Result.success()
        } catch (e: Exception) {
            Log.e("DeleteShiftWorker", "Error deleting all shifts", e)
            Result.retry()
        }
    }
}