package com.hsact.data.repository.shift.remote

import com.hsact.domain.model.Shift

interface ShiftRepositoryRemote {
    fun sync()
    fun saveShift(shift: Shift)
    fun deleteShift(remoteId: String)
    fun deleteAllShifts()
}