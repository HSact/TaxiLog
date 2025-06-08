package com.hsact.taxilog.ui.shift

data class ShiftInputModel(
    val date: String = "",
    val timeStart: String = "",
    val timeEnd: String = "",
    val breakStart: String = "",
    val breakEnd: String = "",
    val earnings: String = "",
    val tips: String = "",
    val wash: String = "",
    val fuelCost: String = "",
    val mileage: String = "",
    val note: String? = null
)