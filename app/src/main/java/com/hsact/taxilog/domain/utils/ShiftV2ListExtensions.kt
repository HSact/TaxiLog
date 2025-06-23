package com.hsact.taxilog.domain.utils

import com.hsact.taxilog.data.model.Shift
import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.domain.utils.DateUtils.getEndOfWeek
import com.hsact.taxilog.domain.utils.DateUtils.getStartOfWeek
import com.hsact.taxilog.ui.shift.ShiftOutputModel
import com.hsact.taxilog.ui.shift.mappers.toUi
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@Suppress("DEPRECATION")
val List<ShiftV2>.toLegacy: List<Shift>
    get() = map { shift -> shift.toLegacy() }

fun List<ShiftV2>.filterByDateRange(
    startDate: LocalDate? = null,
    endDate: LocalDate? = null
): List<ShiftV2> {
    return this.filter { shift ->
        val shiftDate = shift.time.period.start.toLocalDate()
        val afterStart = startDate?.let { !shiftDate.isBefore(it) } != false
        val beforeEnd = endDate?.let { !shiftDate.isAfter(it) } != false
        afterStart && beforeEnd
    }
}

fun List<ShiftV2>.monthlyProfitByDay(date: LocalDate): List<Long> {
    val yearMonth = YearMonth.from(date)
    val daysInMonth = yearMonth.lengthOfMonth()

    val shiftsInMonth = this.filter {
        val d = it.time.period.start.toLocalDate()
        d.monthValue == date.monthValue && d.year == date.year
    }

    val grouped = shiftsInMonth.groupBy {
        it.time.period.start.dayOfMonth
    }

    return List(daysInMonth) { index ->
        val day = index + 1
        grouped[day]?.sumOf { it.profit } ?: 0L
    }
}

fun List<ShiftV2>.weeklyProfitByDay(date: LocalDate): List<Long> {
    val startOfWeek = date.getStartOfWeek()
    val endOfWeek = date.getEndOfWeek()

    val grouped = this
        .filter {
            val d = it.time.period.start.toLocalDate()
            !d.isBefore(startOfWeek) && !d.isAfter(endOfWeek)
        }
        .groupBy {
            it.time.period.start.dayOfWeek.value % 7 // Пн = 1 … Вс = 7 → Пн = 1, ..., Вс = 0
        }

    return List(7) { index -> // 0 = Пн, 6 = Вс
        grouped[index + 1]?.sumOf { it.profit } ?: 0L
    }
}

fun List<ShiftV2>.dailyProfit(date: LocalDate): Long {
    return this
        .filter {
            it.time.period.start.toLocalDate() == date
        }
        .sumOf { it.profit }
}

fun List<ShiftV2>.toUi(locale: Locale): List<ShiftOutputModel> =
    map { shift -> shift.toUi(locale) }

val List<ShiftV2>.profit: List <Long>
    get() = map { it.profit }

val List<ShiftV2>.totalEarnings: Long
    get() = sumOf { it.financeInput.earnings }

val List<ShiftV2>.totalTips: Long
    get() = sumOf { it.financeInput.tips }

val List<ShiftV2>.totalWash: Long
    get() = sumOf { it.financeInput.wash }

val List<ShiftV2>.totalFuelCost: Long
    get() = sumOf { it.financeInput.fuelCost }

val List<ShiftV2>.totalTax: Long
    get() = sumOf { it.financeInput.tax }

val List<ShiftV2>.totalCarExpenses: Long
    get() = sumOf { it.carExpenses }

val List<ShiftV2>.totalProfit: Long
    get() = sumOf { it.profit }

val List<ShiftV2>.totalTime: Long
    get() = sumOf { it.time.totalDuration.toMinutes() }

val List<ShiftV2>.totalExpenses: Long
    get() = sumOf { it.totalExpenses }

val List<ShiftV2>.averageProfit: Long
    get() = takeIf { it.isNotEmpty() }?.sumOf { it.profit }?.div(size) ?: 0

val List<ShiftV2>.averageProfitPerHour: Long
    get() = filter { it.time.totalDuration.toHours() > 0 }
        .takeIf { it.isNotEmpty() }
        ?.let { list ->
            list.sumOf { it.profit.toDouble() / it.time.totalDuration.toHours() } / list.size
        }?.toLong() ?: 0L