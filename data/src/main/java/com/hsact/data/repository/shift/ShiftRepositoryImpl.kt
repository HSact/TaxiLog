package com.hsact.data.repository.shift

import android.util.Log
import com.hsact.data.repository.shift.local.ShiftRepositoryLocal
import com.hsact.data.repository.shift.remote.ShiftRepositoryRemote
import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository
import java.time.LocalDateTime
import javax.inject.Inject

class ShiftRepositoryImpl @Inject constructor(
    private val shiftRepositoryLocal: ShiftRepositoryLocal,
    private val shiftRepositoryRemote: ShiftRepositoryRemote,
) : ShiftRepository {

    override suspend fun sync() {
        shiftRepositoryRemote.sync()
    }

    override suspend fun getAllShifts() =
        shiftRepositoryLocal.getAllShifts()

    override suspend fun getShiftsInRange(
        start: LocalDateTime?,
        end: LocalDateTime?,
    ): List<Shift> {
        return shiftRepositoryLocal.getShiftsInRange(start, end)
    }

    override suspend fun getShift(id: Int): Shift? {
        return shiftRepositoryLocal.getShift(id)
    }

    override suspend fun getLastShift() =
        shiftRepositoryLocal.getLastShift()

    override suspend fun getUnsyncedShifts() =
        shiftRepositoryLocal.getUnsyncedShifts()

    override suspend fun getByRemoteId(remoteId: String) =
        shiftRepositoryLocal.getByRemoteId(remoteId)

    override suspend fun markAsSynced(id: Int, remoteId: String) {
        shiftRepositoryLocal.markAsSynced(id, remoteId)
    }

    override suspend fun insertShift(shift: Shift) {
        val id = shiftRepositoryLocal.insertShift(shift)
        shiftRepositoryRemote.saveShift(shift.withId(id))
    }

    override suspend fun deleteShift(shift: Shift) {
        shiftRepositoryLocal.deleteShift(shift)
        if (shift.remoteId != null) {
            shiftRepositoryRemote.deleteShift(shift.remoteId!!)
            Log.d("ShiftRepository", "Successfully deleted remote shift: remoteId=${shift.remoteId}")
        }
        else {
            Log.d("ShiftRepository", "Shift has no remoteId: id=${shift.id}")
        }
    }

    override suspend fun updateShift(shift: Shift) {
        shiftRepositoryLocal.updateShift(shift)
        if (shift.remoteId != null) {
            shiftRepositoryRemote.saveShift(shift)
            Log.d("ShiftRepository", "Successfully updated remote shift: remoteId=${shift.remoteId}")
        }
        else {
            Log.d("ShiftRepository", "Shift has no remoteId: id=${shift.id}")
        }
    }

    override suspend fun deleteAll() {
        shiftRepositoryLocal.deleteAll()
        shiftRepositoryRemote.deleteAllShifts()
    }

    override suspend fun resetPrimaryKey() =
        shiftRepositoryLocal.resetPrimaryKey()

    private fun Shift.withId(id: Int) = copy(id = id)
}