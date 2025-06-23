package com.hsact.taxilog.domain.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

object DateUtils {

    fun LocalDate.getStartOfWeek(): LocalDate =
        this.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    fun LocalDate.getEndOfWeek(): LocalDate =
        this.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    fun getDayToday(): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        return LocalDate.now().format(dateFormatter)
    }

    fun getCurrentDay(dateReceived: String = ""): Int {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val date = if (dateReceived.isNotEmpty()) {
            try {
                LocalDate.parse(dateReceived, formatter)
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("Invalid date format in shift: $e", e)
                return -1
            }
        } else {
            LocalDate.now()
        }
        return date.dayOfMonth - 1
    }

    fun getCurrentWeek(dateReceived: String = ""): Int {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val date = if (dateReceived.isNotEmpty()) {
            try {
                LocalDate.parse(dateReceived, formatter)
            } catch (e: DateTimeParseException) {
                throw IllegalArgumentException("Invalid date format in shift: $e", e)
                return -1
            }
        } else {
            LocalDate.now()
        }
        return date.get(WeekFields.of(Locale.getDefault()).weekOfYear())
    }

    fun getCurrentMonth(dateReceived: String = ""): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val thisDate = try {
            dateReceived.takeIf { it.isNotEmpty() }?.let {
                LocalDate.parse(it, dateFormatter)
            } ?: LocalDate.now()
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format in shift: $e", e)
            return "0"
        }
        return thisDate.monthValue.toString().padStart(2, '0')
    }

    fun getCurrentYear(dateReceived: String = ""): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val thisDate = try {
            dateReceived.takeIf { it.isNotEmpty() }?.let {
                LocalDate.parse(it, dateFormatter)
            } ?: LocalDate.now()
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format in shift: $e", e)
            return "0"
        }
        return thisDate.year.toString()
    }
}