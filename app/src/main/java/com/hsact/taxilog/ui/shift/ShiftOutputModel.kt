package com.hsact.taxilog.ui.shift

data class ShiftOutputModel(
    val id: Int,
    val carName: String,
    val date: String,           // "01.01.2020"
    val timeRange: String,      // "14:00 – 22:30"
    val duration: String,       // "8 h 30 min"
    val mileageKm: String,      // "142 km"
    val earnings: String,       // "1 200 ₽"
    val tips: String,           // "150 ₽"
    val wash: String,           // "200 ₽"
    val fuelCost: String,       // "850 ₽"
    val rent: String,           // "1 000 ₽"
    val serviceCost: String,    // "250 ₽"
    val profit: String,         // "550 ₽"
    val note: String? = null
)