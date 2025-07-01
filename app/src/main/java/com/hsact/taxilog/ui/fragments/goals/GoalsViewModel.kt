package com.hsact.taxilog.ui.fragments.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.taxilog.domain.utils.DeprecatedDateFormatter
import com.hsact.taxilog.domain.model.Shift
import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.taxilog.domain.usecase.shift.GetShiftsInRangeUseCase
import com.hsact.taxilog.domain.utils.centsToDollars
import com.hsact.taxilog.domain.utils.dailyProfit
import com.hsact.taxilog.domain.utils.monthlyProfitByDay
import com.hsact.taxilog.domain.utils.weeklyProfitByDay
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
    private val getShiftsInRangeUseCase: GetShiftsInRangeUseCase,
) : ViewModel() {

    private val settings: UserSettings = getAllSettingsUseCase.invoke()
    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift?>> = _shifts

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date

    private val _dateLD = MutableStateFlow(LocalDate.now())
    val dateLD: StateFlow<LocalDate> = _dateLD

    private val _daysData = MutableStateFlow(MutableList(31) { 0.0 })
    val daysData: StateFlow<List<Double>> = _daysData

    init {
        _date.value = _dateLD.value.format(DeprecatedDateFormatter)
        viewModelScope.launch {
            _shifts.value = getShiftsInRangeUseCase.invoke(
                _dateLD.value.withDayOfMonth(1).atStartOfDay(),
                _dateLD.value.withDayOfMonth(LocalDate.now().lengthOfMonth()).plusDays(1).atStartOfDay()
            )
            calculateDaysData()
            defineGoals()
        }
    }

    private val _goalDataState = MutableStateFlow(GoalDataState())
    val goalDataState: StateFlow<GoalDataState> = _goalDataState

    var goalMonthString: String? = ""

    private var goalMonth: Double = -1.0
    private var goalWeek: Double = -1.0
    private var goalDay: Double = -1.0

    fun setDate(date: String) {
        _dateLD.value = LocalDate.parse(date, DeprecatedDateFormatter)
        _date.value = date
        calculateDaysData()
        defineGoals()
    }

    fun calculateDaysData() {
        _daysData.value = _shifts.value.monthlyProfitByDay(_dateLD.value).centsToDollars().toMutableList()
    }

    fun defineGoals() {
        goalMonthString = settings.goalPerMonth
        if (goalMonthString.isNullOrEmpty() || goalMonthString == "-1") {
            goalMonthString = ""
//            _goalData.value = createEmptyData()
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

        val dayProfitSum = _shifts.value.dailyProfit(_dateLD.value)
        val weekProfitSum = _shifts.value.weeklyProfitByDay(_dateLD.value).sum()
        val monthProfitSum = _shifts.value.monthlyProfitByDay(_dateLD.value).sum()

        _goalDataState.value = GoalDataState(
            monthGoal = roundTo2(goalMonth),
            weekGoal = roundTo2(goalWeek),
            dayGoal = roundTo2(goalDay),
            dayProgress = dayProfitSum.centsToDollars(),
            weekProgress = weekProfitSum.centsToDollars(),
            monthProgress = monthProfitSum.centsToDollars(),
            todayPercent = roundTo2(
                dayProfitSum.centsToDollars() * 100 / goalDay
            ),
            weekPercent = roundTo2(
                weekProfitSum.centsToDollars() * 100 / goalWeek
            ),
            monthPercent = roundTo2(
                monthProfitSum.centsToDollars() * 100 / goalMonth
            )
        )
    }

    private fun roundTo2(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}