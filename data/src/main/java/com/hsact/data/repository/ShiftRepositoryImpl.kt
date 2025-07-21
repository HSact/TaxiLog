package com.hsact.data.repository

import android.util.Log
import com.hsact.data.db.ShiftDao
import com.hsact.data.firebase.FirebaseShiftDataSource
import com.hsact.data.mappers.toDomain
import com.hsact.data.mappers.toEntity
import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository
import java.time.LocalDateTime
import javax.inject.Inject

class ShiftRepositoryImpl @Inject constructor(
    private val shiftDao: ShiftDao,
    private val firebaseShiftDataSource: FirebaseShiftDataSource,
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
    }

    override suspend fun deleteByLocalId(shift: Shift) {
        shiftDao.deleteById(shift.id)
        Log.d("ShiftRepository", "Remove from Firebase: remoteId=${shift.remoteId} (${shift.remoteId?.length})")
//        shift.remoteId?.let {
//            firebaseShiftDataSource.delete(it)
//        } ?: Log.w("ShiftRepository", "No remoteId to delete from Firebase for shift id=${shift.id}")
    }

    override suspend fun updateShift(shift: Shift) =
        shiftDao.updateShift(shift.toEntity())

    override suspend fun deleteAll() {
        shiftDao.deleteAll()
        firebaseShiftDataSource.deleteAll()
    }

    override suspend fun resetPrimaryKey() =
        shiftDao.resetPrimaryKey()
}