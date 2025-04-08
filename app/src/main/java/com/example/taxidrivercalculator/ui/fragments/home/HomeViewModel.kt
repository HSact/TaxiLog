package com.example.taxidrivercalculator.ui.fragments.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.helpers.DBHelper
import com.example.taxidrivercalculator.helpers.SettingsHelper
import com.example.taxidrivercalculator.helpers.ShiftHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel: ViewModel() {
    private val _shiftData = MutableStateFlow<Map<String, String>>(emptyMap())
    val shiftData: StateFlow<Map<String, String>> = _shiftData

    private val _chartData = MutableStateFlow(MutableList(31) { 0.0 })
    val chartData: StateFlow<List<Double>> = _chartData

    private var _goalData = MutableStateFlow(0.0)
    var goalData: StateFlow<Double> = _goalData

    fun calculateChart (context: Context)
    {
        val db = DBHelper(context, null)
        val cursor = db.getShift()
        cursor!!.moveToLast()
        if (cursor.position==-1)
        {
            return
        }
        cursor.close()
        val shifts = ShiftHelper.makeArray(db)

        val settings = SettingsHelper.getInstance(context)
        _goalData.value = settings.goalPerMonth?.toDoubleOrNull() ?: 0.0
        val tempData = mutableMapOf<Int, Double>()
        for (shift in shifts) {
            if (ShiftHelper.getCurrentMonth(shift.date) == ShiftHelper.getCurrentMonth()) {
                val day = ShiftHelper.getCurrentDay(shift.date)
                tempData[day] = (tempData[day] ?: 0.0) + shift.profit
            }
        }

        var cumulativeSum = 0.0
        _chartData.value = MutableList(31) { day ->
            cumulativeSum += tempData[day] ?: 0.0
            cumulativeSum
        }
    }

    fun calculateShift (context: Context)
    {
        val db = DBHelper(context, null)
        val cursor = db.getShift()
        cursor!!.moveToLast()
        if (cursor.position==-1)
        {
            cursor.close()
            _shiftData.value = createEmptyShift(context)
            return
        }
        val shifts = ShiftHelper.makeArray(db)
        val date = LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        val textDate = cursor.getString(cursor.getColumnIndex(DBHelper.DATE_COl)+0)
        val textEarnings = cursor.getString(cursor.getColumnIndex(DBHelper.EARNINGS_COL)+0)
        val textCosts = ((cursor.getDouble(cursor.getColumnIndex(DBHelper.WASH_COL)+0))+
                (cursor.getDouble(cursor.getColumnIndex(DBHelper.FUEL_COL)+0))).toString()
        val textTime = cursor.getString(cursor.getColumnIndex(DBHelper.TIME_COL)+0) + " " + context.getString(R.string.hours)
        val textTotal = cursor.getString(cursor.getColumnIndex(DBHelper.PROFIT_COL)+0)
        val textPerHour = ShiftHelper.calcAverageEarningsPerHour(shifts.last()).toString()
        val settings = SettingsHelper.getInstance(context)
        var goalMonthString = settings.goalPerMonth?:""

        val goalCurrent =
            if (goalMonthString.isEmpty())
            { context.getString(R.string.n_a) }
            else { ShiftHelper.calculateMonthProgress(date, shifts).toString()}
        cursor.close()
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
    private fun createEmptyShift(context: Context): Map <String, String>
    {
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