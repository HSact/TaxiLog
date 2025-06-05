package com.hsact.taxilog.data.model.shift2

import kotlin.math.roundToLong

data class ShiftMoney(
    val earnings: Long,
    val tips: Long = 0,
    val taxRate: Byte = 0, //0-100%
    val wash: Long = 0,
    val fuelCost: Long,
) {
    val tax: Long
        get() = ((earnings * taxRate).toDouble() / 100).roundToLong()

    val profit: Long
        get() = earnings + tips - tax - wash - fuelCost

    init {
        require(taxRate in 0..100) { "Tax rate must be between 0 and 100" }
    }
}