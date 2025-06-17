package com.hsact.taxilog.ui.fragments.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.hsact.taxilog.R
import com.hsact.taxilog.data.db.DBHelper
import com.hsact.taxilog.data.repository.ShiftRepositoryLegacy
import com.hsact.taxilog.data.utils.DateUtils
import com.hsact.taxilog.data.utils.DeprecatedDateFormatter
import com.hsact.taxilog.data.utils.ShiftStatsUtil
import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.taxilog.domain.usecase.shift.GetAllShiftsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getAllSettingsUseCase: GetAllSettingsUseCase,
    private val getAllShiftsUseCase: GetAllShiftsUseCase
) : ViewModel() {

    private val settings: UserSettings = getAllSettingsUseCase.invoke()

    private val _shiftData = MutableStateFlow<Map<String, String>>(emptyMap())
    val shiftData: StateFlow<Map<String, String>> = _shiftData

    private val _chartData = MutableStateFlow(MutableList(31) { 0.0 })
    val chartData: StateFlow<List<Double>> = _chartData

    private var _goalData = MutableStateFlow(0.0)
    var goalData: StateFlow<Double> = _goalData

    fun calculateChart(context: Context) {
        var shiftRepositoryLegacy = ShiftRepositoryLegacy(DBHelper(context, null))
        val shifts = shiftRepositoryLegacy.getAllShifts()
        if (shifts.isEmpty()) {
            return
        }

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
        var shiftRepositoryLegacy = ShiftRepositoryLegacy(DBHelper(context, null))
        val shifts = shiftRepositoryLegacy.getAllShifts()
        if (shifts.isEmpty()) {
            _shiftData.value = createEmptyShift()
            return
        }
        val shift = shifts.last()
        val date =
            LocalDate.now().withDayOfMonth(1).format(DeprecatedDateFormatter)
        val textDate = shift.date
        val textEarnings = shift.earnings.toString()
        val textCosts = (shift.wash + shift.fuelCost).toString()
        val textTime = shift.time + " " + context.getString(R.string.hours)
        val textTotal = shift.profit.toString()
        val textPerHour = ShiftStatsUtil.calcAverageEarningsPerHour(shifts.last()).toString()
        var goalMonthString = settings.goalPerMonth ?: ""

        val goalCurrent = ShiftStatsUtil.calculateMonthProgress(date, shifts).toString()

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

    private fun createEmptyShift(): Map<String, String> {
        val na = "N/A"
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