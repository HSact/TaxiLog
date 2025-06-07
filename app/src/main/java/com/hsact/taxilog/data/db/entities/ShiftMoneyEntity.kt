package com.hsact.taxilog.data.db.entities

data class ShiftMoneyEntity(
    val earnings: Long,
    val tips: Long = 0,
    val taxRate: Int = 0, //0.00-100.00%
    val wash: Long = 0,
    val fuelCost: Long,
)