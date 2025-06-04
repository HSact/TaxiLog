package com.hsact.taxilog.data.model.shift2

data class ShiftMoney(
    val earnings: Double,
    val tips: Double = 0.0,
    val tax: Double = 0.0,
    val wash: Double = 0.0,
    val fuelCost: Double,
){
    val profit: Double
        get() = earnings + tips - tax - wash - fuelCost
}