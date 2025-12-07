package com.hsact.domain.repository

import com.hsact.domain.model.Shift
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ShiftRepository {
    suspend fun sync()
    fun getAllShifts(): Flow<List<Shift>>
    fun getShiftsInRange(start: LocalDateTime?, end: LocalDateTime?): Flow<List<Shift>>
    fun getShift(id: Int): Flow<Shift?>
    fun getLastShift(): Flow<Shift?>
    suspend fun getUnsyncedShifts(): List<Shift>
    suspend fun getByRemoteId(remoteId: String): Shift?
    suspend fun markAsSynced(id: Int, remoteId: String)
    suspend fun insertShift(shift: Shift)
    suspend fun deleteShift(shift: Shift)
    suspend fun updateShift(shift: Shift)
    suspend fun deleteAll()
    suspend fun resetPrimaryKey()
}