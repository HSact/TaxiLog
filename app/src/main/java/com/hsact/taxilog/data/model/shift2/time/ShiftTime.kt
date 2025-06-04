package com.hsact.taxilog.data.model.shift2.time

import java.time.LocalDateTime
import java.time.Duration

data class ShiftTime(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val breakStart: LocalDateTime? = null,
    val breakEnd: LocalDateTime? = null,
) {
    val totalDuration: Duration
        get() {
            val breakDuration = if (breakStart != null && breakEnd != null) {
                Duration.between(breakStart, breakEnd)
            } else {
                Duration.ZERO
            }
            return Duration.between(start, end).minus(breakDuration)
        }

    fun validate(): List<TimeValidationError> {
        val errors = mutableListOf<TimeValidationError>()
        if (start.isAfter(end)) {
            errors += TimeValidationError.StartAfterEnd
        }
        if (breakStart != null && breakEnd == null) {
            errors += TimeValidationError.BreakEndMissing
        }
        if (breakStart == null && breakEnd != null) {
            errors += TimeValidationError.BreakStartMissing
        }
        if (breakStart != null && breakEnd != null) {
            if (breakStart.isAfter(breakEnd)) {
                errors += TimeValidationError.BreakStartAfterEnd
            }
            if (breakStart.isBefore(start) || breakEnd.isAfter(end)
                || breakStart.isAfter(end) || breakEnd.isBefore(start)) {
                errors += TimeValidationError.BreakOutsideShift
            }
        }
        return errors
    }
    fun isValid(): Boolean = validate().isEmpty()
}