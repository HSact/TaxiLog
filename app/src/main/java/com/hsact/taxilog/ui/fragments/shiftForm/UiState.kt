package com.hsact.taxilog.ui.fragments.shiftForm

import com.hsact.domain.model.Shift

data class UiState(
    val id: Int = 0,
    val editShift: Shift? = null,
    val date: String = "",
    val timeBegin: String = "",
    val timeEnd: String = "",
    val breakBegin: String = "",
    val breakEnd: String = "",
    val onlineTime: Long = 0,
    val breakTime: Long = 0,
    val totalTime: Long = 0,
    val earnings: Double = 0.0,
    val tips: Double = 0.0,
    val wash: Double = 0.0,
    val fuelCost: Double = 0.0,
    val mileage: Double = 0.0,
    val profit: Double = 0.0,
    val note: String = "",
)