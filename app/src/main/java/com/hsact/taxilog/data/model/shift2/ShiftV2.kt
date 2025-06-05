package com.hsact.taxilog.data.model.shift2

import com.hsact.taxilog.data.model.Shift
import com.hsact.taxilog.data.model.shift2.car.CarSnapshot
import com.hsact.taxilog.data.model.shift2.time.ShiftTime
import com.hsact.taxilog.data.utils.DeprecatedDateFormatter
import com.hsact.taxilog.data.utils.centsToDollars
import com.hsact.taxilog.data.utils.round

data class ShiftV2(
    val id: Int,
    val carId: Int,
    val carSnapshot: CarSnapshot,
    val time: ShiftTime,
    val money: ShiftMoney,
    val mileage: Long,
    val note: String? = null
){
    fun toShift(): Shift {
        return  Shift(
            id = id,
            date = time.start.toLocalDate().format(DeprecatedDateFormatter),
            time = time.totalDuration.toHours().toString(),
            earnings = money.earnings.centsToDollars(),
            wash = money.wash.centsToDollars(),
            fuelCost = money.fuelCost.centsToDollars(),
            mileage = (mileage.toDouble()/1000).round(),
            profit = money.profit.centsToDollars()
            )
    }
}