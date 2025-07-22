package com.hsact.data.sync.worker

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.hsact.domain.model.Shift

object FirebaseWorkerManager {
    fun enqueueSync(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncAllShiftsWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
    fun enqueueSave(shift: Shift, context: Context) {
        val request = OneTimeWorkRequestBuilder<SaveShiftWorker>()
            .setInputData(workDataOf("shiftId" to shift.id))
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }

    fun enqueueDelete(remoteId: String, context: Context) {
        val request = OneTimeWorkRequestBuilder<DeleteShiftWorker>()
            .setInputData(workDataOf("remoteId" to remoteId))
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
    fun enqueueDeleteAll(context: Context) {
        val request = OneTimeWorkRequestBuilder<DeleteAllShiftsWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}