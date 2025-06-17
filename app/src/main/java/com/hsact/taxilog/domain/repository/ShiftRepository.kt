package com.hsact.taxilog.domain.repository

import com.hsact.taxilog.domain.model.ShiftV2
import java.time.LocalDateTime

interface ShiftRepository {
    suspend fun getAllShifts(): List<ShiftV2>
    suspend fun getShiftsInRange(start: LocalDateTime?, end: LocalDateTime?): List<ShiftV2>
    suspend fun getShift(id: Int): ShiftV2?
    suspend fun getLastShift(): ShiftV2?
    suspend fun insertShift(shift: ShiftV2)
    suspend fun deleteShift(shift: ShiftV2)
    suspend fun updateShift(shift: ShiftV2)
    suspend fun deleteAll()
    suspend fun resetPrimaryKey()
}