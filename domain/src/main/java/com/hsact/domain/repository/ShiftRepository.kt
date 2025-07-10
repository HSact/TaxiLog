package com.hsact.domain.repository

import com.hsact.domain.model.Shift
import java.time.LocalDateTime

interface ShiftRepository {
    suspend fun getAllShifts(): List<Shift>
    suspend fun getShiftsInRange(start: LocalDateTime?, end: LocalDateTime?): List<Shift>
    suspend fun getShift(id: Int): Shift?
    suspend fun getLastShift(): Shift?
    suspend fun getUnsyncedShifts(): List<Shift>
    suspend fun getByRemoteId(remoteId: String): Shift?
    suspend fun markAsSynced(id: Int)
    suspend fun insertShift(shift: Shift)
    suspend fun deleteShift(shift: Shift)
    suspend fun deleteById(id: Int)
    suspend fun updateShift(shift: Shift)
    suspend fun deleteAll()
    suspend fun resetPrimaryKey()
}