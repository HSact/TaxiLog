package com.hsact.data.firebase

import android.util.Log
import com.hsact.domain.model.Shift
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
        val remoteIds = remoteShifts.mapNotNull { it.remoteId }.toSet()

        for (remoteShift in remoteShifts) {
            val remoteId = remoteShift.remoteId ?: continue
            val localShift = shiftRepository.getByRemoteId(remoteId)

            val newMeta = remoteShift.meta.copy(isSynced = true)
            val shiftWithSynced = remoteShift.copy(meta = newMeta)

            if (localShift == null) {
                shiftRepository.insertShift(shiftWithSynced.withNewId())
                Log.d("Sync", "Inserted remote shift: $remoteId")
            } else if (localShift.meta.updatedAt < remoteShift.meta.updatedAt) {
                shiftRepository.updateShift(shiftWithSynced.copy(id = localShift.id))
                Log.d("Sync", "Updated remote shift: $remoteId")
            }
        }

        // Removing shifts from local, which are not in Firebase — but only if isSynced == true
        val allLocal = shiftRepository.getAllShifts()
        val toDelete = allLocal.filter { local ->
            val remoteId = local.remoteId
            remoteId != null && local.meta.isSynced && remoteId !in remoteIds
        }

        for (shift in toDelete) {
            shiftRepository.deleteByLocalId(shift)
            Log.d("Sync", "Deleted orphaned shift: remoteId=${shift.remoteId}")
        }
    }

    private suspend fun syncToFirebase() {
        val localUnsyncedShifts = shiftRepository.getUnsyncedShifts()
        for (localShift in localUnsyncedShifts) {
            val remoteId = firebaseShiftDataSource.save(localShift)
            if (remoteId != null) {
                shiftRepository.markAsSynced(localShift.id, remoteId)
                Log.d("Sync", "Synced local shift: remoteId=$remoteId")
            }
            else {
                Log.w("Sync", "Failed to sync local shift: id=${localShift.id}")
            }
        }
    }

    private fun Shift.withNewId(): Shift = this.copy(id = 0)
}