package com.hsact.taxilog.data.repository

import com.hsact.taxilog.data.db.ShiftDao
import com.hsact.taxilog.data.mappers.toDomain
import com.hsact.taxilog.data.mappers.toEntity
import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.domain.repository.ShiftRepository
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
    ): List<ShiftV2> {
        val startString = start?.toString() // ISO-8601
        val endString = end?.toString()
        val entities = shiftDao.getShiftsInRange(startString, endString)
        return entities.map { it.toDomain() }
    }

    override suspend fun getShift(id: Int) =
        shiftDao.getShiftById(id)?.toDomain()

    override suspend fun getLastShift() =
        shiftDao.getLastShift()?.toDomain()

    override suspend fun insertShift(shift: ShiftV2) =
        shiftDao.insertShift(shift.toEntity())

    override suspend fun deleteShift(shift: ShiftV2) =
        shiftDao.deleteShift(shift.toEntity())

    override suspend fun updateShift(shift: ShiftV2) =
        shiftDao.updateShift(shift.toEntity())

    override suspend fun deleteAll() =
        shiftDao.deleteAll()

    override suspend fun resetPrimaryKey() =
        shiftDao.resetPrimaryKey()
}