package com.hsact.taxilog.data.model.shift2.time

sealed class TimeValidationError {
    object StartAfterEnd : TimeValidationError()
    object BreakEndMissing : TimeValidationError()
    object BreakStartMissing : TimeValidationError()
    object BreakStartAfterEnd : TimeValidationError()
    object BreakOutsideShift: TimeValidationError()
}