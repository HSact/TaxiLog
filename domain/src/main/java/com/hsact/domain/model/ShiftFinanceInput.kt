package com.hsact.domain.model

import kotlin.math.roundToLong

data class ShiftFinanceInput(
    val earnings: Long, //in cents
    val tips: Long = 0, //in cents
    val taxRate: Int = 0, //0.00-100.00% * 100
    val wash: Long = 0, //in cents
    val fuelCost: Long, //in cents
) {
    val tax: Long   //in cents
        get() = ((earnings * taxRate).toDouble() / 10000).roundToLong()

    val profit: Long    //in cents
        get() = earnings + tips - tax - fuelCost

    init {
        require(taxRate in 0..10000) { "Tax rate must be between 0 and 10000" }
    }
}