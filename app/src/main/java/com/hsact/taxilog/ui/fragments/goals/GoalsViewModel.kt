package com.hsact.taxilog.ui.fragments.goals

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.taxilog.data.utils.DeprecatedDateFormatter
import com.hsact.taxilog.data.utils.ShiftStatsUtil
import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.taxilog.domain.usecase.shift.GetAllShiftsUseCase
import com.hsact.taxilog.domain.utils.toLegacy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    getAllSettingsUseCase: GetAllSettingsUseCase,
    private val getAllShiftsUseCase: GetAllShiftsUseCase,
) : ViewModel() {

    private val settings: UserSettings = getAllSettingsUseCase.invoke()
    private val _shifts = MutableStateFlow<List<ShiftV2>>(emptyList())
    val shifts: StateFlow<List<ShiftV2?>> = _shifts

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date

    init {
        viewModelScope.launch {
            _shifts.value = getAllShiftsUseCase.invoke()
            calculateDaysData()
            defineGoals()
        }
        _date.value = LocalDate.now().format(DeprecatedDateFormatter)
    }

    private val _goalData = MutableStateFlow<Map<String, Double>>(emptyMap())
    val goalData: StateFlow<Map<String, Double>> = _goalData

    var goalMonthString: String? = ""

    private val _daysData = MutableStateFlow(MutableList(31) { 0.0 })
    val daysData: StateFlow<List<Double>> = _daysData

    private var goalMonth: Double = -1.0
    private var goalWeek: Double = -1.0
    private var goalDay: Double = -1.0

    fun setDate (date: String) {
        _date.value = date
    }

    @SuppressLint("DefaultLocale")
    fun calculateDaysData() {
        val shiftsLegacy = _shifts.value.toLegacy
        val parts = date.value.split(".")
        if (parts.size != 3) return
        val month = parts[1]
        val year = parts[2]
        val newData = MutableList(31) { day ->
            val dayString = String.format("%02d", day + 1)
            val formattedDate = "$dayString.$month.$year"
            ShiftStatsUtil.calculateDayProgress(formattedDate, shiftsLegacy)
        }
        _daysData.value = newData
    }

    fun defineGoals() {
        val date = date.value
        goalMonthString = settings.goalPerMonth
        if (goalMonthString.isNullOrEmpty() || goalMonthString == "-1") {
            goalMonthString = ""
            _goalData.value = createEmptyData()
            return
        }
        goalMonth = goalMonthString!!.toDouble()
        val denominatorWeek = 4.5
        goalWeek = goalMonth / denominatorWeek
        val denominatorDay = when (settings.schedule) {
            "7/0" -> 30.0
            "6/1" -> 25.7
            "5/2" -> 21.4
            else -> 30.0
        }
        goalDay = goalMonth / denominatorDay
        val shiftsLegacy = _shifts.value.toLegacy
        _goalData.value = mapOf(
            "monthGoal" to roundTo2(goalMonth),
            "weekGoal" to roundTo2(goalWeek),
            "dayGoal" to roundTo2(goalDay),
            "dayProgress" to (roundTo2(ShiftStatsUtil.calculateDayProgress(date, shiftsLegacy))),
            "weekProgress" to (roundTo2(ShiftStatsUtil.calculateWeekProgress(date, shiftsLegacy))),
            "monthProgress" to (roundTo2(ShiftStatsUtil.calculateMonthProgress(date, shiftsLegacy))),
            "todayPercent" to (roundTo2(
                ShiftStatsUtil.calculateDayProgress(
                    date,
                    shiftsLegacy
                ) * 100 / goalDay
            )),
            "weekPercent" to (roundTo2(
                ShiftStatsUtil.calculateWeekProgress(
                    date,
                    shiftsLegacy
                ) * 100 / goalWeek
            )),
            "monthPercent" to (roundTo2(
                ShiftStatsUtil.calculateMonthProgress(
                    date,
                    shiftsLegacy
                ) * 100 / goalMonth
            ))
        )
    }

    private fun createEmptyData(): Map<String, Double> {
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