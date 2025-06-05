package com.hsact.taxilog.data.model.shift2

data class ShiftMoney(
    val earnings: Long,
    val tips: Long = 0,
    val tax: Long = 0,
    val wash: Long = 0,
    val fuelCost: Long,
){
    val profit: Long
        get() = earnings + tips - tax - wash - fuelCost
}