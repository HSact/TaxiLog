package com.hsact.data.repository

import android.util.Log
import com.hsact.data.db.ShiftDao
import com.hsact.data.mappers.toDomain
import com.hsact.data.mappers.toEntity
import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository
import com.hsact.domain.sync.RemoteShiftController
import java.time.LocalDateTime
import javax.inject.Inject

class ShiftRepositoryImpl @Inject constructor(
    private val shiftDao: ShiftDao,
    private val shiftRemoteController: RemoteShiftController,
) : ShiftRepository {

    override suspend fun getAllShifts() =
        shiftDao.getAllShifts()
            .map { it.toDomain() }

    override suspend fun getShiftsInRange(
        start: LocalDateTime?,
        end: LocalDateTime?,
    ): List<Shift> {
        val startString = start?.toString() // ISO-8601
        val endString = end?.toString()
        val entities = shiftDao.getShiftsInRange(startString, endString)
        return entities.map { it.toDomain() }
    }

    override suspend fun getShift(id: Int): Shift? {
        val shiftEntity = shiftDao.getShiftById(id)
        Log.d("ShiftRepository", "getShift: id=$id, remoteId=${shiftEntity?.remoteId}")
        return shiftEntity?.toDomain()
    }

    override suspend fun getLastShift() =
        shiftDao.getLastShift()?.toDomain()

    override suspend fun getUnsyncedShifts() =
        shiftDao.getUnsyncedShifts()
            .map { it.toDomain() }

    override suspend fun getByRemoteId(remoteId: String) =
        shiftDao.getByRemoteId(remoteId)?.toDomain()

    override suspend fun markAsSynced(id: Int, remoteId: String) {
        val shift = shiftDao.getShiftById(id)
        if (shift == null) {
            Log.w("ShiftRepository", "Shift with id $id not found")
            return
        }
        val newMeta = shift.meta.copy(isSynced = true)
        shiftDao.updateShift(shift.copy(meta = newMeta, remoteId = remoteId))
    }

    override suspend fun insertShift(shift: Shift) {
        shiftDao.insertShift(shift.toEntity())
        Log.d("ShiftRepository", "Insert local shift: id=${shift.id} remoteId=${shift.remoteId?: "no remoteId"}")
    }

    override suspend fun deleteShift(shift: Shift) {
        shiftDao.deleteById(shift.id)
        Log.d("ShiftRepository", "Remove local shift: id=${shift.id} remoteId=${shift.remoteId?: "no remoteId"}")
        shift.remoteId?.let { remoteId ->
            try {
                shiftRemoteController.deleteShift(remoteId)
                Log.d("ShiftRepository", "Successfully removed remote shift: remoteId=$remoteId")
            } catch (e: Exception) {
                Log.e("ShiftRepository", "Error deleting shift remotely", e)
            }
        }
    }

    override suspend fun updateShift(shift: Shift) {
        shiftDao.updateShift(shift.toEntity())
        Log.d("ShiftRepository", "Update local shift: id=${shift.id} remoteId=${shift.remoteId?: "no remoteId"}")
        shift.remoteId?.let { remoteId ->
            try {
                shiftRemoteController.saveShift(shift)
                Log.d("ShiftRepository", "Successfully updated remote shift: remoteId=$remoteId")
            } catch (e: Exception) {
                Log.e("ShiftRepository", "Error updating shift remotely", e)
            }
        }
    }

    override suspend fun deleteAll() {
        shiftDao.deleteAll()
        Log.d("ShiftRepository", "Deleted all shifts locally")
        shiftRemoteController.deleteAllShifts()
    }

    override suspend fun resetPrimaryKey() =
        shiftDao.resetPrimaryKey()
}