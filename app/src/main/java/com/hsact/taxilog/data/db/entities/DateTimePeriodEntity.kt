package com.hsact.taxilog.data.db.entities

import java.time.LocalDateTime

data class DateTimePeriodEntity(
    val start: LocalDateTime,
    val end: LocalDateTime
)