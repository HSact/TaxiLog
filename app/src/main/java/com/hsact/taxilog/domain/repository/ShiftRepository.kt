package com.hsact.taxilog.domain.repository

import com.hsact.taxilog.domain.model.Shift
import java.time.LocalDateTime

interface ShiftRepository {
    suspend fun getAllShifts(): List<Shift>
    suspend fun getShiftsInRange(start: LocalDateTime?, end: LocalDateTime?): List<Shift>
    suspend fun getShift(id: Int): Shift?
    suspend fun getLastShift(): Shift?
    suspend fun insertShift(shift: Shift)
    suspend fun deleteShift(shift: Shift)
    suspend fun updateShift(shift: Shift)
    suspend fun deleteAll()
    suspend fun resetPrimaryKey()
}