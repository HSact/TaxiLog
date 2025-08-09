package com.hsact.data.repository.shift.local

import android.util.Log
import com.hsact.data.db.ShiftDao
import com.hsact.data.mappers.toDomain
import com.hsact.data.mappers.toEntity
import com.hsact.domain.model.Shift
import java.time.LocalDateTime
import javax.inject.Inject

class ShiftRepositoryLocalImpl @Inject constructor(
    private val shiftDao: ShiftDao,
) : ShiftRepositoryLocal {

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
        Log.d("ShiftRepositoryLocal", "getShift: id=$id, remoteId=${shiftEntity?.remoteId}")
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
            Log.w("ShiftRepositoryLocal", "Shift with id $id not found")
            return
        }
        val newMeta = shift.meta.copy(isSynced = true)
        shiftDao.updateShift(shift.copy(meta = newMeta, remoteId = remoteId))
    }

    override suspend fun insertShift(shift: Shift) {
        shiftDao.insertShift(shift.toEntity())
        Log.d("ShiftRepositoryLocal", "Insert local shift: id=${shift.id} remoteId=${shift.remoteId?: "no remoteId"}")
    }

    override suspend fun deleteShift(shift: Shift) {
        shiftDao.deleteById(shift.id)
        Log.d("ShiftRepositoryLocal", "Remove local shift: id=${shift.id} remoteId=${shift.remoteId?: "no remoteId"}")
    }

    override suspend fun updateShift(shift: Shift) {
        shiftDao.updateShift(shift.toEntity())
        Log.d("ShiftRepositoryLocal", "Update local shift: id=${shift.id} remoteId=${shift.remoteId?: "no remoteId"}")
    }

    override suspend fun deleteAll() {
        shiftDao.deleteAll()
        Log.d("ShiftRepositoryLocal", "Deleted all shifts locally")
    }

    override suspend fun resetPrimaryKey() =
        shiftDao.resetPrimaryKey()
}