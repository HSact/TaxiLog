package com.hsact.taxilog.domain.utils

import com.hsact.taxilog.domain.model.ShiftV2

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