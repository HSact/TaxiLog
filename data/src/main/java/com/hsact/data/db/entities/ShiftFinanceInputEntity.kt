package com.hsact.data.db.entities

data class ShiftFinanceInputEntity(
    val earnings: Long, //in cents
    val tips: Long = 0, //in cents
    val taxRate: Int = 0, //0.00-100.00% * 100
    val wash: Long = 0, //in cents
    val fuelCost: Long, //in cents
)