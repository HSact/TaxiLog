package com.hsact.taxilog.domain.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

fun LocalDate.getStartOfWeek(): LocalDate =
    this.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

fun LocalDate.getEndOfWeek(): LocalDate =
    this.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

fun LocalDateTime.toShortDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return this.format(formatter)
}

fun LocalDateTime.toShortTime(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return this.format(formatter)
}