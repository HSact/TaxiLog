package com.hsact.data.sync

import android.util.Log
import com.hsact.data.firebase.datasource.FirebaseShiftDataSource
import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Manages the synchronization of [Shift] data between the local repository and Firebase.
 *
 * This class coordinates bidirectional sync: pulling remote changes from Firebase and
 * pushing local modifications to the cloud.
 */
class ShiftSyncManager @Inject constructor(
    private val firebaseShiftDataSource: FirebaseShiftDataSource,
    private val shiftRepository: ShiftRepository,
) {
    /**
     * Performs a full synchronization cycle.
     *
     * First pulls updates from Firebase and merges them locally, then pushes any
     * unsynced local shifts to Firebase.
     */
    suspend fun sync() {
        syncFromFirebase()
        syncToFirebase()
    }

    /**
     * Synchronizes a single shift by its local ID.
     *
     * Fetches the shift from the local repository and saves it to Firebase.
     * If successful, the local shift is marked as synchronized.
     *
     * @param id The local ID of the shift to synchronize.
     */
    suspend fun syncShift(id: Int) {
        val shift = shiftRepository.getShift(id).first() ?: return
        val remoteId = firebaseShiftDataSource.save(shift)
        if (remoteId != null) {
            shiftRepository.markAsSynced(id, remoteId)
            Log.d("Sync", "Synced single shift: id=$id remoteId=$remoteId")
        }
    }

    /**
     * Pulls all shifts from Firebase and merges them into the local repository.
     *
     * - New remote shifts are inserted locally.
     * - Existing shifts are updated if the remote version is newer ShiftMeta.updatedAt.
     * - If local and remote timestamps are equal but the local shift is not marked as synced,
     *   it updates the local status synced.
     * - Orphaned local shifts (marked as synced but missing in Firebase) are deleted.
     */
    private suspend fun syncFromFirebase() {
        try {
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
                } else if (!localShift.meta.isSynced) {
                    // If timestamps are equal but local is not marked as synced, fix it
                    shiftRepository.markAsSynced(localShift.id, remoteId)
                    Log.d("Sync", "Marked identical shift as synced: $remoteId")
                }
            }

            // Removing shifts from local, which are not in Firebase — but only if isSynced == true
            val allLocal = shiftRepository.getAllShifts().first()
            val toDelete = allLocal.filter { local ->
                val remoteId = local.remoteId
                remoteId != null && local.meta.isSynced && remoteId !in remoteIds
            }

            for (shift in toDelete) {
                shiftRepository.deleteShift(shift)
                Log.d("Sync", "Deleted orphaned shift: remoteId=${shift.remoteId}")
            }
        } catch (e: Exception) {
            Log.e("Sync", "Failed to sync from Firebase", e)
        }
    }

    /**
     * Pushes all local unsynced shifts to Firebase.
     *
     * Iterates through shifts that haven't been synchronized yet and attempts to save
     * them to the cloud. On success, each shift is marked as synchronized locally.
     */
    private suspend fun syncToFirebase() {
        try {
            val localUnsyncedShifts = shiftRepository.getUnsyncedShifts()
            for (localShift in localUnsyncedShifts) {
                val remoteId = firebaseShiftDataSource.save(localShift)
                if (remoteId != null) {
                    shiftRepository.markAsSynced(localShift.id, remoteId)
                    Log.d("Sync", "Synced local shift: remoteId=$remoteId")
                } else {
                    Log.w("Sync", "Failed to sync local shift: id=${localShift.id}")
                }
            }
        } catch (e: Exception) {
            Log.e("Sync", "Failed to sync to Firebase", e)
        }
    }

    private fun Shift.withNewId(): Shift = this.copy(id = 0)
}