package com.hsact.data.db.entities

import java.time.LocalDateTime

data class DateTimePeriodEntity(
    val start: LocalDateTime,
    val end: LocalDateTime
)