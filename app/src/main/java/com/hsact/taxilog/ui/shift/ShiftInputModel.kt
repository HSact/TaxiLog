package com.hsact.taxilog.ui.shift

data class ShiftInputModel(
    var date: String = "",
    var timeStart: String = "",
    var timeEnd: String = "",
    var breakStart: String = "",
    var breakEnd: String = "",
    var earnings: String = "",
    var tips: String = "",
    var wash: String = "",
    var fuelCost: String = "",
    var mileage: String = "",
    var note: String? = null
)