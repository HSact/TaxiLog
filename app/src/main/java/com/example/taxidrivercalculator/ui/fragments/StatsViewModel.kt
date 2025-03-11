package com.example.taxidrivercalculator.ui.fragments

import androidx.lifecycle.ViewModel
import com.example.taxidrivercalculator.helpers.Shift
import com.example.taxidrivercalculator.helpers.ShiftHelper

class StatsViewModel : ViewModel() {
    var shifts = mutableListOf<Shift>()
    var shiftsOrigin = mutableListOf<Shift>()
    var startDate: String? = null
    var endDate: String? = null

    val shiftsCount: String get() = shifts.size.toString()
    val avErPh: String get() = ShiftHelper.calcAverageEarningsPerHour(shifts).toString()
    val avProfitPh: String get() = ShiftHelper.calcAverageProfitPerHour(shifts).toString()
    val avDuration: String get() = ShiftHelper.calcAverageShiftDuration(shifts).toString()
    val avMileage: String get() = ShiftHelper.calcAverageMileage(shifts).toString()
    val totalDuration: String get() = ShiftHelper.calcTotalShiftDuration(shifts).toString()
    val totalMileage: String get() = ShiftHelper.calcTotalMileage(shifts).toString()
    val totalWash: String get() = ShiftHelper.calcTotalWash(shifts).toString()
    val totalEarnings: String get() = ShiftHelper.calcTotalEarnings(shifts).toString()
    val totalProfit: String get() = ShiftHelper.calcTotalProfit(shifts).toString()
    val avFuel: String get() = ShiftHelper.calcAverageFuelCost(shifts).toString()
    val totalFuel: String get() = ShiftHelper.calcTotalFuelCost(shifts).toString()

    fun updateShifts()
    {
        shifts = ShiftHelper.filterShiftsByDatePeriod(shiftsOrigin.toMutableList(), startDate,
            endDate).toMutableList()
    }
}