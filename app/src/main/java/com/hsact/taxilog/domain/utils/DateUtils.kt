package com.hsact.taxilog.domain.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

fun LocalDate.getStartOfWeek(): LocalDate =
    this.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

fun LocalDate.getEndOfWeek(): LocalDate =
    this.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))