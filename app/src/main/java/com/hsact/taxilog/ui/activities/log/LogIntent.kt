package com.hsact.taxilog.ui.activities.log

import com.hsact.taxilog.domain.model.ShiftV2

sealed class LogIntent {
    //object LoadShifts : LogIntent()
    data class DeleteShift(val shift: ShiftV2) : LogIntent()
    data class EditShift(val shift: ShiftV2) : LogIntent()
    object DeleteAllShifts : LogIntent()
}