package com.hsact.domain.utils

import com.hsact.domain.model.Shift
import java.time.LocalDate
import java.time.YearMonth

fun List<Shift>.filterByDateRange(
    startDate: LocalDate? = null,
    endDate: LocalDate? = null
): List<Shift> {
    return this.filter { shift ->
        val shiftDate = shift.time.period.start.toLocalDate()
        val afterStart = startDate?.let { !shiftDate.isBefore(it) } != false
        val beforeEnd = endDate?.let { !shiftDate.isAfter(it) } != false
        afterStart && beforeEnd
    }
}

fun List<Shift>.monthlyProfitByDay(date: LocalDate): List<Long> {
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

fun List<Shift>.weeklyProfitByDay(date: LocalDate): List<Long> {
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

fun List<Shift>.dailyProfit(date: LocalDate): Long {
    return this
        .filter {
            it.time.period.start.toLocalDate() == date
        }
        .sumOf { it.profit }
}


val List<Shift>.profit: List <Long>
    get() = map { it.profit }

val List<Shift>.totalEarnings: Long
    get() = sumOf { it.financeInput.earnings }

val List<Shift>.totalTips: Long
    get() = sumOf { it.financeInput.tips }

val List<Shift>.totalWash: Long
    get() = sumOf { it.financeInput.wash }

val List<Shift>.totalMileage: Long
    get() = sumOf { it.carSnapshot.mileage}

val List<Shift>.totalFuelCost: Long
    get() = sumOf { it.financeInput.fuelCost }

val List<Shift>.totalTax: Long
    get() = sumOf { it.financeInput.tax }

val List<Shift>.totalCarExpenses: Long
    get() = sumOf { it.carExpenses }

val List<Shift>.totalProfit: Long
    get() = sumOf { it.profit }

val List<Shift>.totalTime: Long
    get() = sumOf { it.time.totalDuration.toMinutes() }

val List<Shift>.totalExpenses: Long
    get() = sumOf { it.totalExpenses }

val List<Shift>.averageDuration: Long
    get() = if (isNotEmpty()) sumOf { it.time.totalDuration.toMinutes() } / size else 0

val List<Shift>.averageMileage: Long
    get() = if (isNotEmpty()) sumOf { it.carSnapshot.mileage } / size else 0

val List<Shift>.averageWash: Long
    get() = if (isNotEmpty()) sumOf { it.financeInput.wash } / size else 0

val List<Shift>.averageFuelCost: Long
    get() = if (isNotEmpty()) sumOf { it.financeInput.fuelCost } / size else 0

val List<Shift>.averageProfit: Long
    get() = takeIf { it.isNotEmpty() }?.sumOf { it.profit }?.div(size) ?: 0

val List<Shift>.averageEarningsPerHour: Long
    get() = filter { it.time.totalDuration.toHours() > 0 }
        .takeIf { it.isNotEmpty() }
        ?.let { list ->
            list.sumOf { it.financeInput.earnings.toDouble() / it.time.totalDuration.toHours() } / list.size
        }?.toLong() ?: 0L

val List<Shift>.averageProfitPerHour: Long
    get() = filter { it.time.totalDuration.toHours() > 0 }
        .takeIf { it.isNotEmpty() }
        ?.let { list ->
            list.sumOf { it.profit.toDouble() / it.time.totalDuration.toHours() } / list.size
        }?.toLong() ?: 0L