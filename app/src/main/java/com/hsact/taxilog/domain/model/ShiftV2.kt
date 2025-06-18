package com.hsact.taxilog.domain.model

import com.hsact.taxilog.data.model.Shift
import com.hsact.taxilog.domain.model.car.CarSnapshot
import com.hsact.taxilog.domain.model.time.ShiftTime
import com.hsact.taxilog.data.utils.DeprecatedDateFormatter
import com.hsact.taxilog.data.utils.centsToDollars
import com.hsact.taxilog.data.utils.round

data class ShiftV2(
    val id: Int,
    val carId: Int,
    val carSnapshot: CarSnapshot,
    val time: ShiftTime,
    val financeInput: ShiftFinanceInput,
    val note: String? = null,
) {
    val carExpenses: Long
        get() = carSnapshot.rentCost + carSnapshot.serviceCost

    val totalExpenses: Long
        get() = carExpenses + financeInput.wash + financeInput.fuelCost + financeInput.tax

    val earningsPerHour: Long
        get() = financeInput.earnings / time.totalDuration.toHours()

    val profit: Long
        get() = financeInput.earnings + financeInput.tips - financeInput.tax - financeInput.wash - financeInput.fuelCost - carExpenses

    val profitPerHour: Long
        get() = profit / time.totalDuration.toHours()

    @Suppress("DEPRECATION")
    fun toLegacy(): Shift {
        return Shift(
            id = id,
            date = time.period.start.toLocalDate().format(DeprecatedDateFormatter),
            time = time.totalDuration.toHours().toString(),
            earnings = financeInput.earnings.centsToDollars(),
            wash = financeInput.wash.centsToDollars(),
            fuelCost = financeInput.fuelCost.centsToDollars(),
            mileage = (carSnapshot.mileage.toDouble() / 1000).round(),
            profit = financeInput.profit.centsToDollars()
        )
    }
}