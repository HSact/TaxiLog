package com.example.taxidrivercalculator.ui.fragments.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.data.db.DBHelper
import com.example.taxidrivercalculator.data.repository.ShiftRepository
import com.example.taxidrivercalculator.data.utils.DateUtils
import com.example.taxidrivercalculator.helpers.SettingsHelper
import com.example.taxidrivercalculator.data.utils.ShiftHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {
    private val _shiftData = MutableStateFlow<Map<String, String>>(emptyMap())
    val shiftData: StateFlow<Map<String, String>> = _shiftData

    private val _chartData = MutableStateFlow(MutableList(31) { 0.0 })
    val chartData: StateFlow<List<Double>> = _chartData

    private var _goalData = MutableStateFlow(0.0)
    var goalData: StateFlow<Double> = _goalData

    fun calculateChart(context: Context) {
        var shiftRepository = ShiftRepository(DBHelper(context, null))
        val shifts = shiftRepository.getAllShifts()
        if (shifts.isEmpty()) {
            return
        }

        val settings = SettingsHelper.getInstance(context)
        _goalData.value = settings.goalPerMonth?.toDoubleOrNull() ?: 0.0
        val tempData = mutableMapOf<Int, Double>()
        for (shift in shifts) {
            if (DateUtils.getCurrentMonth(shift.date) == DateUtils.getCurrentMonth()) {
                val day = DateUtils.getCurrentDay(shift.date)
                tempData[day] = (tempData[day] ?: 0.0) + shift.profit
            }
        }

        var cumulativeSum = 0.0
        _chartData.value = MutableList(31) { day ->
            cumulativeSum += tempData[day] ?: 0.0
            cumulativeSum
        }
    }

    fun calculateShift(context: Context) {
        var shiftRepository = ShiftRepository(DBHelper(context, null))
        val shifts = shiftRepository.getAllShifts()
        if (shifts.isEmpty()) {
            _shiftData.value = createEmptyShift(context)
            return
        }
        val shift = shifts.last()
        val date =
            LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        val textDate = shift.date
        val textEarnings = shift.earnings.toString()
        val textCosts = (shift.wash + shift.fuelCost).toString()
        val textTime = shift.time + " " + context.getString(R.string.hours)
        val textTotal = shift.profit.toString()
        val textPerHour = ShiftHelper.calcAverageEarningsPerHour(shifts.last()).toString()
        val settings = SettingsHelper.getInstance(context)
        var goalMonthString = settings.goalPerMonth ?: ""

        val goalCurrent =
            if (goalMonthString.isEmpty()) {
                context.getString(R.string.n_a)
            } else {
                ShiftHelper.calculateMonthProgress(date, shifts).toString()
            }

        _shiftData.value = mapOf(
            "date" to textDate,
            "earnings" to textEarnings,
            "costs" to textCosts,
            "time" to textTime,
            "total" to textTotal,
            "perHour" to textPerHour,
            "goal" to goalMonthString,
            "goalCurrent" to goalCurrent,
        )
    }

    private fun createEmptyShift(context: Context): Map<String, String> {
        val na = context.getString(R.string.n_a)
        return mapOf(
            "date" to na,
            "earnings" to na,
            "costs" to na,
            "time" to na,
            "total" to na,
            "perHour" to na,
            "goal" to na,
            "goalCurrent" to na,
        )
    }
}