package com.hsact.taxilog.ui.shift

data class ShiftInputModel(
    var carId: Int = 0,
    var carName: String = "",
    var date: String = "",
    var timeStart: String = "",
    var timeEnd: String = "",
    var breakStart: String = "",
    var breakEnd: String = "",
    var taxRate: String = "",
    var rentCost: String = "",
    var serviceCost: String = "",
    var consumption: String = "",
    var earnings: String = "",
    var tips: String = "",
    var wash: String = "",
    var fuelCost: String = "",
    var mileage: String = "",
    var note: String? = null
)