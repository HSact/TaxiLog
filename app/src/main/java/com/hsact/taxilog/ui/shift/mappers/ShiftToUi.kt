package com.hsact.taxilog.ui.shift.mappers

import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.ui.shift.ShiftOutputModel
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.*

fun ShiftV2.toUiModel(locale: Locale): ShiftOutputModel {
    val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy", locale)
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm", locale)

    val start = time.period.start
    val end = time.period.end

    return ShiftOutputModel(
        id = id,
        carName = carSnapshot.name,
        date = start.format(formatterDate),
        timeRange = "${start.format(formatterTime)} – ${end.format(formatterTime)}",
        duration = formatDuration(time.totalDuration, locale),
        mileageKm = when (locale.language) {
            "ru" -> "${carSnapshot.mileage / 1000}"
            else -> "${carSnapshot.mileage / 1000}"
        },
        earnings = financeInput.earnings.centsToCurrency(locale),
        earningsPerHour = earningsPerHour.centsToCurrency(locale),
        tips = financeInput.tips.centsToCurrency(locale),
        wash = financeInput.wash.centsToCurrency(locale),
        fuelCost = financeInput.fuelCost.centsToCurrency(locale),
        rent = carSnapshot.rentCost.centsToCurrency(locale),
        serviceCost = carSnapshot.serviceCost.centsToCurrency(locale),
        profit = profit.centsToCurrency(locale),
        profitPerHour = profitPerHour.centsToCurrency(locale),
        note = note
    )
}

private fun formatDuration(duration: Duration, locale: Locale): String {
    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60)

    return when (locale.language) {
        "ru" -> buildString {
            if (hours > 0) append("$hours")
            if (minutes > 0 || hours == 0L) append("$minutes")
        }.trim()
        else -> buildString {
            if (hours > 0) append("$hours h ")
            if (minutes > 0 || hours == 0L) append("$minutes")
        }.trim()
    }
}

private fun Long.centsToCurrency(locale: Locale): String {
    val dollars = this.toDouble() / 100
    return when (locale.language) {
        "ru" -> String.format(locale, "%,.0f", dollars).replace(',', ' ')
        else -> String.format(locale, "$%,.0f", dollars)
    }
}