package com.hsact.taxilog.ui.shift.mappers

import com.hsact.taxilog.domain.model.Shift
import com.hsact.taxilog.domain.model.settings.CurrencySymbolMode
import com.hsact.taxilog.ui.shift.ShiftOutputModel
import java.text.NumberFormat
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.*

fun Shift.toUi(locale: Locale, currencySymbol: CurrencySymbolMode): ShiftOutputModel {
    val formatterDate = DateTimeFormatter.ofPattern("dd.MM.yyyy", locale)
    val formatterTime = DateTimeFormatter.ofPattern("HH:mm", locale)

    val kmString = when (locale.language) {
        "ru" -> "км"
        else -> "km"
    }
    val start = time.period.start
    val end = time.period.end

    return ShiftOutputModel(
        id = id,
        carName = carSnapshot.name,
        dateBegin = start.format(formatterDate),
        dateEnd = end.format(formatterDate),
        timeRange = "${start.format(formatterTime)} – ${end.format(formatterTime)}",
        timeBegin = start.format(formatterTime),
        timeEnd = end.format(formatterTime),
        timeRestBegin = if (time.rest != null) time.rest.start.format(formatterTime) else "",
        timeRestEnd = if (time.rest != null) time.rest.end.format(formatterTime) else "",
        duration = formatDuration(time.totalDuration, locale),
        mileageKm = "${carSnapshot.mileage / 1000} $kmString",
        earnings = financeInput.earnings.centsToCurrency(locale, currencySymbol),
        earningsPerHour = earningsPerHour.centsToCurrency(locale, currencySymbol),
        earningsPerKm = "${earningsPerKm.centsToCurrency(locale, currencySymbol)}/$kmString",
        tips = financeInput.tips.centsToCurrency(locale, currencySymbol),
        wash = financeInput.wash.centsToCurrency(locale, currencySymbol),
        fuelCost = financeInput.fuelCost.centsToCurrency(locale, currencySymbol),
        fuelConsumption = consumption.millilitersToLiters(locale),
        rent = carSnapshot.rentCost.centsToCurrency(locale, currencySymbol),
        serviceCost = carSnapshot.serviceCost.centsToCurrency(locale, currencySymbol),
        tax = financeInput.tax.centsToCurrency(locale, currencySymbol),
        totalExpenses = totalExpenses.centsToCurrency(locale, currencySymbol),
        profit = profit.centsToCurrency(locale, currencySymbol),
        profitPerHour = profitPerHour.centsToCurrency(locale, currencySymbol),
        profitPerKm = "${profitPerKm.centsToCurrency(locale, currencySymbol)}/$kmString",
        profitMarginPercent = "$profitMarginPercent %",
        note = note
    )
}

fun Long.centsToCurrency(locale: Locale, currencySymbol: CurrencySymbolMode): String {
    val amount = this.toDouble() / 100

    val numberFormat = NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    val formattedAmount = numberFormat.format(amount)
    val symbol = currencySymbol.toSymbol()

    val postfixSet = setOf(CurrencySymbolMode.RUB, CurrencySymbolMode.EUR)

    return if (currencySymbol in postfixSet) {
        "$formattedAmount $symbol"
    } else {
        "$symbol $formattedAmount"
    }
}

fun Long.minutesToHours(locale: Locale): String {
    return formatDuration(Duration.ofMinutes(this), locale)
}

fun Long.millisToHours(locale: Locale): String {
    return formatDuration(Duration.ofMillis(this), locale)
}

fun Long.metersToKilometers(locale: Locale): String {
    val kilometers = this.toDouble() / 1000
    return when (locale.language) {
        "ru" -> String.format(locale, "%,.1f км", kilometers).replace(',', '.')
        else -> String.format(locale, "%,.1f km", kilometers)
    }
}

fun Long.millilitersToLiters(locale: Locale): String {
    val kilometers = this.toDouble() / 1000
    return when (locale.language) {
        "ru" -> String.format(locale, "%,.1f л", kilometers).replace(',', '.')
        else -> String.format(locale, "%,.1f L", kilometers)
    }
}

private fun formatDuration(duration: Duration, locale: Locale): String {
    val hours = duration.toHours()
    val minutes = (duration.toMinutes() % 60)

    return when (locale.language) {
        "ru" -> buildString {
            if (hours > 0) append("$hours ч ")
            if (minutes > 0 || hours == 0L) append("$minutes мин")
        }.trim()

        else -> buildString {
            if (hours > 0) append("$hours h ")
            if (minutes > 0 || hours == 0L) append("$minutes min")
        }.trim()
    }
}