package com.hsact.data.repository.shift.remote

import android.content.Context
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
    }

    override fun saveShift(shift: Shift) {
        FirebaseWorkerManager.enqueueSave(shift, context)
    }

    override fun deleteShift(remoteId: String) {
        FirebaseWorkerManager.enqueueDelete(remoteId, context)
    }

    override fun deleteAllShifts() {
        FirebaseWorkerManager.enqueueDeleteAll(context)
    }
}