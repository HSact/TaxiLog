package com.example.taxidrivercalculator.ui.fragments.goals

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taxidrivercalculator.helpers.DBHelper
import com.example.taxidrivercalculator.helpers.ShiftHelper
import java.math.BigDecimal
import java.math.RoundingMode

class GoalsViewModel: ViewModel() {
    private val _goalData = MutableLiveData<Map<String, Double>>()
    val goalData: LiveData<Map<String, Double>> get() = _goalData

    var pickedDate: String = ""
    var goalMonthString: String? = ""

    private var goalMonth: Double = -1.0
    private var goalWeek: Double = -1.0
    private var goalDay: Double = -1.0

    fun defineGoals(date: String, context: Context)
    {
        val settings = context.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        goalMonthString = settings?.getString("Goal_per_month", "")
        if (goalMonthString.isNullOrEmpty() || goalMonthString == "-1")
        {
            goalMonthString = ""
            _goalData.value = createEmptyData()
            return
        }
        if (pickedDate.isEmpty())
        {
            pickedDate = date
        }
        goalMonth = goalMonthString!!.toDouble()
        val schedule = settings.getString("Schedule_text", "")
        val denominatorWeek = 4.5
        goalWeek = goalMonth / denominatorWeek
        val denominatorDay = when (schedule) {
            "7/0" -> 30.0
            "6/1" -> 25.7
            "5/2" -> 21.4
            "0" -> 30.0
            else -> 30.0
        }
        goalDay = goalMonth / denominatorDay
        val db = DBHelper (context, null)
        _goalData.value = mapOf(
            "todayPercent" to (roundTo2(ShiftHelper.calculateDayProgress(date, db) * 100 / goalDay)),
            "weekPercent" to (roundTo2(ShiftHelper.calculateWeekProgress(date, db) * 100 / goalWeek)),
            "monthPercent" to (roundTo2(ShiftHelper.calculateMonthProgress(date, db) * 100 / goalMonth))
        )
    }

    private fun createEmptyData(): Map <String, Double>
    {
        val zeroValue = 0.0
        return mapOf(
        "todayPercent" to zeroValue,
        "weekPercent" to zeroValue,
        "monthPercent" to zeroValue
        )
    }
    private fun roundTo2(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}