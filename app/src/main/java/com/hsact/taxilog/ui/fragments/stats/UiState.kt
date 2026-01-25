package com.hsact.taxilog.ui.fragments.stats

data class UiState(
    val startDate: String = "",
    val endDate: String = "",
    val shiftsCount: String = "",
    val avErPh: String = "",
    val avProfitPh: String = "",
    val avProfit: String = "",
    val avDuration: String = "",
    val avMileage: String = "",
    val avFuel: String = "",
    val avWash: String = "",
    val totalDuration: String = "",
    val totalMileage: String = "",
    val totalWash: String = "",
    val totalService: String = "",
    val totalEarnings: String = "",
    val totalProfit: String = "",
    val totalTips: String = "",
    val totalTax: String = "",
    val totalCarExpenses: String = "",
    val totalFuel: String = "",
)