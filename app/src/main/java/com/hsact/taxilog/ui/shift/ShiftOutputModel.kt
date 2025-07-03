package com.hsact.taxilog.ui.shift

data class ShiftOutputModel(
    val id: Int,
    val carName: String,
    val dateBegin: String,          // "01.01.2020"
    val dateEnd: String,            // "02.01.2020"
    val timeRange: String,          // "14:00 – 22:30"
    val timeBegin: String,          // "14:00"
    val timeEnd: String,            // "22:30"
    val timeRestBegin: String,      // "16:00"
    val timeRestEnd: String,        // "18:00"
    val duration: String,           // "8 h 30 min"
    val mileageKm: String,          // "142 km"
    val earnings: String,           // "1 200 ₽"
    val earningsPerHour: String,    // "100 ₽"
    val earningsPerKm: String,      // "8 ₽/km"
    val tips: String,               // "150 ₽"
    val wash: String,               // "200 ₽"
    val fuelCost: String,           // "850 ₽"
    val fuelConsumption: String,    // "10.5 L"
    val rent: String,               // "1 000 ₽"
    val serviceCost: String,        // "250 ₽"
    val tax: String,                // "0 ₽"
    val totalExpenses: String,      // "100 ₽"
    val profit: String,             // "550 ₽"
    val profitPerHour: String,      // "70 ₽"
    val profitPerKm: String,           // "4 ₽/km"
    val profitMarginPercent: String,   // "45 %"
    val note: String? = null
)