package com.hsact.taxilog.ui.shift.mappers

import com.hsact.taxilog.domain.shift2.ShiftFinanceInput
import com.hsact.taxilog.domain.shift2.ShiftV2
import com.hsact.taxilog.domain.shift2.car.CarSnapshot
import com.hsact.taxilog.domain.shift2.time.DateTimePeriod
import com.hsact.taxilog.domain.shift2.time.ShiftTime
import com.hsact.taxilog.ui.shift.ShiftInputModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToLong

fun ShiftInputModel.toDomain(
    carId: Int,
    carSnapshot: CarSnapshot,
    taxRate: Int
): ShiftV2 {
    val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val formatterTime = DateTimeFormatter.ofPattern("H:mm")

    val parsedDate = LocalDate.parse(date, formatterDate)
    val startTime = LocalTime.parse(timeStart, formatterTime)
    val endTime = LocalTime.parse(timeEnd, formatterTime)
    val startDateTime = LocalDateTime.of(parsedDate, startTime)
    val endDateTime = if (endTime.isBefore(startTime)) {
        // Shift ends on the next day
        LocalDateTime.of(parsedDate.plusDays(1), endTime)
    } else {
        LocalDateTime.of(parsedDate, endTime)
    }

    val restPeriod = if (breakStart.isNotBlank() && breakEnd.isNotBlank()) {
        val restStart = LocalTime.parse(breakStart, formatterTime)
        val restEnd = LocalTime.parse(breakEnd, formatterTime)
        val restStartDateTime = LocalDateTime.of(parsedDate, restStart)
        val restEndDateTime = if (restEnd.isBefore(restStart)) {
            LocalDateTime.of(parsedDate.plusDays(1), restEnd)
        } else {
            LocalDateTime.of(parsedDate, restEnd)
        }
        DateTimePeriod(restStartDateTime, restEndDateTime)
    } else {
        null
    }

    return ShiftV2(
        id = 0,
        carId = carId,
        carSnapshot = carSnapshot.copy(
            mileage = ((mileage.toDoubleOrNull() ?: (0.0 * 1000))).toLong()
        ),
        time = ShiftTime(
            period = DateTimePeriod(startDateTime, endDateTime),
            rest = restPeriod
        ),
        financeInput = ShiftFinanceInput(
            earnings = rubToCents(earnings),
            tips = rubToCents(tips),
            wash = rubToCents(wash),
            fuelCost = rubToCents(fuelCost),
            taxRate = taxRate
        ),
        note = note
    )
}

private fun rubToCents(input: String): Long {
    return ((input.replace(",", ".").toDoubleOrNull() ?: 0.0) * 100).roundToLong()
}