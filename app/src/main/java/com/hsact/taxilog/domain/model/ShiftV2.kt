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
    val totalCarExpenses: Long
        get() = carSnapshot.rentCost + carSnapshot.serviceCost

    val profit: Long
        get() = financeInput.earnings + financeInput.tips - financeInput.tax - financeInput.wash - financeInput.fuelCost - totalCarExpenses

    fun toShift(): Shift {
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