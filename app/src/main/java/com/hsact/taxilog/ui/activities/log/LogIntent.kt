package com.hsact.taxilog.ui.activities.log

import com.hsact.taxilog.domain.model.Shift

sealed class LogIntent {
    //object LoadShifts : LogIntent()
    data class DeleteShift(val shift: Shift) : LogIntent()
    data class EditShift(val shift: Shift) : LogIntent()
    object DeleteAllShifts : LogIntent()
}