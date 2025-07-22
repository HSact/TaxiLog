package com.hsact.domain.model.time

import java.time.Duration
import java.time.LocalDateTime

data class DateTimePeriod(
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    val duration: Duration
        get() = Duration.between(start, end)

    fun isValid(): Boolean = start.isBefore(end)
}