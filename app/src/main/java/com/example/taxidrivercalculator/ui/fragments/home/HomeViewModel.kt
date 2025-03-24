package com.example.taxidrivercalculator.ui.fragments.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.helpers.DBHelper
import com.example.taxidrivercalculator.helpers.SettingsHelper
import com.example.taxidrivercalculator.helpers.ShiftHelper

class HomeViewModel: ViewModel() {
    private val _shiftData = MutableLiveData<Map<String, String>>()
    val shiftData: LiveData<Map<String, String>> get() = _shiftData

    fun calculateShift (context: Context)
    {
        val db = DBHelper(context, null)
        //db.addShift("1.01.1001", 8.0, 1337.0, 228.0, 1488.0, 30.0, 300.0)
        val cursor = db.getShift()
        cursor!!.moveToLast()
        if (cursor.position==-1)
        {
            cursor.close()
            _shiftData.value = createEmptyShift(context)
            return
        }
        val shifts = ShiftHelper.makeArray(db)

        val textDate = cursor.getString(cursor.getColumnIndex(DBHelper.DATE_COl)+0)
        val textEarnings = cursor.getString(cursor.getColumnIndex(DBHelper.EARNINGS_COL)+0)
        val textCosts = ((cursor.getDouble(cursor.getColumnIndex(DBHelper.WASH_COL)+0))+
                (cursor.getDouble(cursor.getColumnIndex(DBHelper.FUEL_COL)+0))).toString()
        val textTime = cursor.getString(cursor.getColumnIndex(DBHelper.TIME_COL)+0) + " " + context.getString(R.string.hours)
        val textTotal = cursor.getString(cursor.getColumnIndex(DBHelper.PROFIT_COL)+0)
        val textPerHour = ShiftHelper.calcAverageEarningsPerHour(shifts.last()).toString()
        val settings = SettingsHelper.getInstance(context)
        val goalMonthString = settings.goalPerMonth

        val textGoal =
            if (goalMonthString.isNullOrEmpty())
            { context.getString(R.string.n_a) }
        else { ShiftHelper.calculateMonthProgress(shifts.last().date, db).toString() +
                    " " + context.getString(R.string.of) + " " + goalMonthString }
        cursor.close()

        _shiftData.value = mapOf(
            "date" to textDate,
            "earnings" to textEarnings,
            "costs" to textCosts,
            "time" to textTime,
            "total" to textTotal,
            "perHour" to textPerHour,
            "goal" to textGoal
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
            "goal" to na
        )
    }
}