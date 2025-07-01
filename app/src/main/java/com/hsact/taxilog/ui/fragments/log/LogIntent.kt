package com.hsact.taxilog.ui.fragments.log

import com.hsact.taxilog.domain.model.Shift

sealed class LogIntent {
    //object LoadShifts : LogIntent()
    data class DeleteShift(val shift: Shift) : LogIntent()
    object UpdateList: LogIntent()
    object DeleteAllShifts : LogIntent()
}