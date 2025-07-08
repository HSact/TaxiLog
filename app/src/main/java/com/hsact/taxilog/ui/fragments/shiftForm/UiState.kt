package com.hsact.taxilog.ui.fragments.shiftForm

import com.hsact.domain.model.Shift

data class UiState(
    val id: Int = 0,
    val editShift: Shift? = null,
    var date: String = "",
    var timeBegin: String = "",
    var timeEnd: String = "",
    var breakBegin: String = "",
    var breakEnd: String = "",
    var onlineTime: Long = 0,
    var breakTime: Long = 0,
    var totalTime: Long = 0,
    var earnings: Double = 0.0,
    var tips: Double = 0.0,
    var wash: Double = 0.0,
    var fuelCost: Double = 0.0,
    var mileage: Double = 0.0,
    var profit: Double = 0.0,
    var note: String = "",
)