package com.hsact.taxilog.domain.shift2.time

sealed class TimeValidationError {
    object StartAfterEnd : TimeValidationError()
    object BreakStartAfterEnd : TimeValidationError()
    object BreakOutsideShift: TimeValidationError()
}