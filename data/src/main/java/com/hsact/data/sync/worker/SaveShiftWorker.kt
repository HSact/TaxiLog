package com.hsact.data.sync.worker

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveShiftWorker(
    context: Context,
    workerParams: WorkerParameters,
) : BaseFirebaseWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val id = inputData.getInt("shiftId", -1)
        if (id == -1) return@withContext Result.failure()

        val shift = getShiftByIdUseCase(id) ?: return@withContext Result.failure()

        return@withContext try {
            updateShiftUseCase(shift)
            Log.d("UpdateShiftWorker", "Shift updated: $id")
            Result.success()
        } catch (e: Exception) {
            Log.e("UpdateShiftWorker", "Update shift error: $id", e)
            Result.retry()
        }
    }
}