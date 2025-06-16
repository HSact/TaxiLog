package com.hsact.taxilog.ui.fragments.goals

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.hsact.taxilog.data.db.DBHelper
import com.hsact.taxilog.data.repository.ShiftRepositoryLegacy
import com.hsact.taxilog.data.utils.ShiftStatsUtil
import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.domain.usecase.settings.GetAllSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    getAllSettingsUseCase: GetAllSettingsUseCase
) : ViewModel() {
    private val settings: UserSettings = getAllSettingsUseCase.invoke()

    private val _goalData = MutableStateFlow<Map<String, Double>>(emptyMap())
    val goalData: StateFlow<Map<String, Double>> = _goalData

    var pickedDate: String = ""
    var goalMonthString: String? = ""

    private val _daysData = MutableStateFlow(MutableList(31) { 0.0 })
    val daysData: StateFlow<List<Double>> = _daysData

    private var goalMonth: Double = -1.0
    private var goalWeek: Double = -1.0
    private var goalDay: Double = -1.0

    @SuppressLint("DefaultLocale")
    fun calculateDaysData(date: String, context: Context) {
        var shiftRepositoryLegacy = ShiftRepositoryLegacy(DBHelper(context, null))
        if (pickedDate.isEmpty()) {
            pickedDate = date
        }
        val shifts = shiftRepositoryLegacy.getAllShifts()
        val parts = date.split(".")
        if (parts.size != 3) return
        val month = parts[1]
        val year = parts[2]
        val newData = MutableList(31) { day ->
            val dayString = String.format("%02d", day + 1)
            val formattedDate = "$dayString.$month.$year"
            ShiftStatsUtil.calculateDayProgress(formattedDate, shifts)
        }
        _daysData.value = newData
    }

    fun defineGoals(date: String, context: Context)
    {
        var shiftRepositoryLegacy = ShiftRepositoryLegacy(DBHelper(context, null))
        goalMonthString = settings.goalPerMonth
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
        val denominatorWeek = 4.5
        goalWeek = goalMonth / denominatorWeek
        val denominatorDay = when (settings.schedule) {
            "7/0" -> 30.0
            "6/1" -> 25.7
            "5/2" -> 21.4
            else -> 30.0
        }
        goalDay = goalMonth / denominatorDay
        val shifts = shiftRepositoryLegacy.getAllShifts()
        _goalData.value = mapOf(
            "monthGoal" to roundTo2(goalMonth),
            "weekGoal" to roundTo2(goalWeek),
            "dayGoal" to roundTo2(goalDay),
            "dayProgress" to (roundTo2(ShiftStatsUtil.calculateDayProgress(date, shifts))),
            "weekProgress" to (roundTo2(ShiftStatsUtil.calculateWeekProgress(date, shifts))),
            "monthProgress" to (roundTo2(ShiftStatsUtil.calculateMonthProgress(date, shifts))),
            "todayPercent" to (roundTo2(ShiftStatsUtil.calculateDayProgress(date, shifts) * 100 / goalDay)),
            "weekPercent" to (roundTo2(ShiftStatsUtil.calculateWeekProgress(date, shifts) * 100 / goalWeek)),
            "monthPercent" to (roundTo2(ShiftStatsUtil.calculateMonthProgress(date, shifts) * 100 / goalMonth))
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