package com.hsact.taxilog.domain.shift2

import kotlin.math.roundToLong

data class ShiftMoney(
    val earnings: Long,
    val tips: Long = 0,
    val taxRate: Int = 0, //0.00-100.00%
    val wash: Long = 0,
    val fuelCost: Long,
) {
    val tax: Long
        get() = ((earnings * taxRate).toDouble() / 10000).roundToLong()

    val profit: Long
        get() = earnings + tips - tax - wash - fuelCost

    init {
        require(taxRate in 0..10000) { "Tax rate must be between 0 and 100" }
    }
}