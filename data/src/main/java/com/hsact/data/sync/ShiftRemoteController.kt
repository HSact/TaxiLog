package com.hsact.data.sync

import com.hsact.domain.model.Shift

interface ShiftRemoteController {
    fun sync()
    fun saveShift(shift: Shift)
    fun deleteShift(remoteId: String)
    fun deleteAllShifts()
}