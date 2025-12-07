package com.hsact.data.repository.shift.local

import android.util.Log
import com.hsact.data.db.ShiftDao
import com.hsact.data.mappers.toDomain
import com.hsact.data.mappers.toEntity
import com.hsact.domain.model.Shift
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class ShiftRepositoryLocalImpl @Inject constructor(
    private val shiftDao: ShiftDao,
) : ShiftRepositoryLocal {

    override fun getAllShifts(): Flow<List<Shift>> =
        shiftDao.getAllShifts()
            .map { list -> list.map { it.toDomain() } }

    override fun getShiftsInRange(
        start: LocalDateTime?,
        end: LocalDateTime?,
    ): Flow<List<Shift>> {
        val startString = start?.toString()
        val endString = end?.toString()
        return shiftDao.getShiftsInRange(startString, endString)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getShift(id: Int): Flow<Shift?> =
        shiftDao.getShiftById(id)
            .map { entity ->
                Log.d(
                    "ShiftRepositoryLocal",
                    "getShift: id=$id, remoteId=${entity?.remoteId}"
                )
                entity?.toDomain()
            }

    override fun getLastShift(): Flow<Shift?> =
        shiftDao.getLastShift()
            .map { it?.toDomain() }

    override suspend fun getUnsyncedShifts() =
        shiftDao.getUnsyncedShifts()
            .map { it.toDomain() }

    override suspend fun getByRemoteId(remoteId: String) =
        shiftDao.getByRemoteId(remoteId)?.toDomain()

    override suspend fun markAsSynced(id: Int, remoteId: String) {
        val shift = shiftDao.getShiftById(id).first()
        if (shift == null) {
            Log.w("ShiftRepositoryLocal", "Shift with id $id not found")
            return
        }
        val newMeta = shift.meta.copy(isSynced = true)
        shiftDao.updateShift(shift.copy(meta = newMeta, remoteId = remoteId))
    }

    override suspend fun insertShift(shift: Shift): Int {
        val id = shiftDao.insertShift(shift.toEntity())
        Log.d("ShiftRepositoryLocal", "Insert local shift: id=${id} remoteId=${shift.remoteId?: "no remoteId"}")
        return id.toInt()
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