package com.hsact.data.repository.shift.remote

import android.content.Context
import android.util.Log
import com.hsact.data.sync.worker.FirebaseWorkerManager
import com.hsact.domain.model.Shift
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShiftRepositoryRemoteImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ShiftRepositoryRemote {
    override fun sync() {
        FirebaseWorkerManager.enqueueSync(context)
        Log.d("ShiftRepositoryRemote", "sync() called")
    }

    override fun saveShift(shift: Shift) {
        FirebaseWorkerManager.enqueueSave(shift, context)
        Log.d("ShiftRepositoryRemote", "saveShift() called for shift: ${shift.id}")
    }

    override fun deleteShift(remoteId: String) {
        FirebaseWorkerManager.enqueueDelete(remoteId, context)
        Log.d("ShiftRepositoryRemote", "deleteShift() called for shift: $remoteId")
    }

    override fun deleteAllShifts() {
        FirebaseWorkerManager.enqueueDeleteAll(context)
        Log.d("ShiftRepositoryRemote", "deleteAllShifts() called")
    }
}