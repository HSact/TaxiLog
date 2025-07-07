package com.hsact.domain.model.time

import java.time.Duration

data class ShiftTime(
    val period: DateTimePeriod,
    val rest: DateTimePeriod? = null,
) {
    val totalDuration: Duration
        get() {
            return if (rest != null) period.duration.minus(rest.duration)
            else period.duration
        }

    fun validate(): List<TimeValidationError> {
        val errors = mutableListOf<TimeValidationError>()
        if (period.start.isAfter(period.end)) {
            errors += TimeValidationError.StartAfterEnd
        }
        if (rest != null) {
            if (!rest.isValid()) {
                errors += TimeValidationError.BreakStartAfterEnd
            }
            if (rest.start.isBefore(period.start) || rest.end.isAfter(period.end)) {
                errors += TimeValidationError.BreakOutsideShift
            }
        }
        return errors
    }

    fun isValid(): Boolean = validate().isEmpty()
}