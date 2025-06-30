package com.hsact.taxilog.domain.model

import com.hsact.taxilog.domain.model.car.CarSnapshot
import com.hsact.taxilog.domain.model.time.ShiftTime

data class Shift(
    val id: Int,
    val carId: Int,
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

    val profit: Long
        get() = financeInput.earnings + financeInput.tips - financeInput.tax - financeInput.wash - financeInput.fuelCost - carExpenses

    val profitPerHour: Long
        get() = profit / time.totalDuration.toHours()
}