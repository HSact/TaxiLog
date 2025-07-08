package com.hsact.domain.model

import com.hsact.domain.model.car.CarSnapshot
import com.hsact.domain.model.time.ShiftTime
import kotlin.math.roundToInt

data class Shift(
    val id: Int,
    val remoteId: String?,
    val carId: Int,
    val meta: ShiftMeta,
    val carSnapshot: CarSnapshot,
    val time: ShiftTime,
    val financeInput: ShiftFinanceInput,
    val note: String? = null,
) {
    val carExpenses: Long
        get() = carSnapshot.rentCost + carSnapshot.serviceCost

    val consumption: Long
        get() = (carSnapshot.fuelConsumption * carSnapshot.mileage) / 100_000

    val totalExpenses: Long
        get() = carExpenses + financeInput.wash + financeInput.fuelCost + financeInput.tax

    val earningsPerHour: Long
        get() = financeInput.earnings / time.totalDuration.toHours()

    val earningsPerKm: Long
        get() = if (carSnapshot.mileage > 0)
            financeInput.earnings * 1000 / carSnapshot.mileage
        else 0

    val profit: Long
        get() = financeInput.earnings + financeInput.tips - financeInput.tax - financeInput.wash - financeInput.fuelCost - carExpenses

    val profitPerHour: Long
        get() = profit / time.totalDuration.toHours()

    val profitMarginPercent: Int
        get() = if (financeInput.earnings + financeInput.tips > 0)
            ((profit.toDouble() / (financeInput.earnings + financeInput.tips)) * 100).roundToInt()
        else 0

    val profitPerKm: Long
        get() = if (carSnapshot.mileage > 0)
            profit * 1000 / carSnapshot.mileage
        else 0

    val tipsIsNotZero: Boolean
        get() = financeInput.tips != 0L

    val rentIsNotZero: Boolean
        get() = carSnapshot.rentCost != 0L

    val washIsNotZero: Boolean
        get() = financeInput.wash != 0L

    val serviceIsNotZero: Boolean
        get() = carSnapshot.serviceCost != 0L

    val taxIsNotZero: Boolean
        get() = financeInput.tax != 0L
}