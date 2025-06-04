package com.hsact.taxilog.data.model.shift2

import com.hsact.taxilog.data.model.Shift
import com.hsact.taxilog.data.model.shift2.time.ShiftTime
import com.hsact.taxilog.data.utils.DeprecatedDateFormatter

data class ShiftV2(
    val id: Int,
    val car: Car,
    val time: ShiftTime,
    val money: ShiftMoney,
    val mileage: Double,
    val note: String? = null
){
    fun toShift(): Shift {
        return  Shift(
            id = id,
            date = time.start.toLocalDate().format(DeprecatedDateFormatter.formatter),
            time = time.totalDuration.toHours().toString(),
            earnings = money.earnings,
            wash = money.wash,
            fuelCost = money.fuelCost,
            mileage = mileage,
            profit = money.profit
            )
    }
}