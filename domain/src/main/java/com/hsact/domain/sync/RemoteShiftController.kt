package com.hsact.domain.sync

import com.hsact.domain.model.Shift

interface RemoteShiftController {
    fun sync()
    fun saveShift(shift: Shift)
    fun deleteShift(remoteId: String)
    fun deleteAllShifts()
}