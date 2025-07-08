package com.hsact.data.repository

import com.hsact.data.db.ShiftDao
import com.hsact.data.mappers.toDomain
import com.hsact.data.mappers.toEntity
import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository
import java.time.LocalDateTime
import javax.inject.Inject

class ShiftRepositoryImpl @Inject constructor(
    private val shiftDao: ShiftDao,
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

    override suspend fun getShift(id: Int) =
        shiftDao.getShiftById(id)?.toDomain()

    override suspend fun getLastShift() =
        shiftDao.getLastShift()?.toDomain()

    override suspend fun insertShift(shift: Shift) =
        shiftDao.insertShift(shift.toEntity())

    override suspend fun deleteShift(shift: Shift) =
        shiftDao.deleteShift(shift.toEntity())

    override suspend fun updateShift(shift: Shift) =
        shiftDao.updateShift(shift.toEntity())

    override suspend fun deleteAll() =
        shiftDao.deleteAll()

    override suspend fun resetPrimaryKey() =
        shiftDao.resetPrimaryKey()
}