package com.hsact.taxilog.data.model.shift2.time

import java.time.LocalDateTime

data class DateTimePeriod(
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    fun isValid(): Boolean = start.isBefore(end)
}