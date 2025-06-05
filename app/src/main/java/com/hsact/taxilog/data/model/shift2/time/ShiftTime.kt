package com.hsact.taxilog.data.model.shift2.time

import java.time.Duration

data class ShiftTime(
    val period: DateTimePeriod,
    val rest: DateTimePeriod? = null,
) {
    val totalDuration: Duration
        get() {
            val breakDuration = if (rest != null) {
                Duration.between(rest.start, rest.end)
            } else {
                Duration.ZERO
            }
            return Duration.between(period.start, period.end).minus(breakDuration)
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