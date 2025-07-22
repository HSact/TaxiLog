package com.hsact.domain.model.time

sealed class TimeValidationError {
    object StartAfterEnd : TimeValidationError()
    object BreakStartAfterEnd : TimeValidationError()
    object BreakOutsideShift : TimeValidationError()
}