package com.hsact.data.firebase

import android.util.Log
import com.hsact.domain.repository.ShiftRepository
import javax.inject.Inject

class ShiftSyncManager @Inject constructor(
    private val firebaseShiftDataSource: FirebaseShiftDataSource,
    private val shiftRepository: ShiftRepository,
) {
    suspend fun sync() {
        syncFromFirebase()
        syncToFirebase()
    }

    private suspend fun syncFromFirebase() {
        val remoteShifts = firebaseShiftDataSource.getAll()
        for (remoteShift in remoteShifts) {
            val remoteId = remoteShift.remoteId ?: continue
            val localShift = shiftRepository.getByRemoteId(remoteId)

            val newMeta = remoteShift.meta.copy(isSynced = true)
            val shiftWithSynced = remoteShift.copy(meta = newMeta)

            if (localShift == null) {
                shiftRepository.insertShift(shiftWithSynced)
                Log.d("Sync", "Inserted remote shift: $remoteId")
            } else {
                // saving local ID
                val updated = shiftWithSynced.copy(id = localShift.id)
                shiftRepository.updateShift(updated)
                Log.d("Sync", "Updated remote shift: $remoteId")
            }
        }
    }

    private suspend fun syncToFirebase() {
        val localUnsyncedShifts = shiftRepository.getUnsyncedShifts()
        for (localShift in localUnsyncedShifts) {
            firebaseShiftDataSource.save(localShift)
            shiftRepository.markAsSynced(localShift.id)
        }
    }
}