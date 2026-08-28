package com.hsact.taxilog.ui.fragments.log

import com.hsact.domain.model.Shift

sealed class LogIntent {
    data class DeleteShift(
        val shift: Shift,
    ) : LogIntent()

    object UpdateList : LogIntent()

    object DeleteAllShifts : LogIntent()

    data class ChangeFilter(val period: LogFilterPeriod) : LogIntent()

    data class ChangeSort(val sortOrder: LogSortOrder) : LogIntent()
}
