package com.hsact.taxilog.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.domain.usecase.shift.GetLastShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftsInRangeUseCase
import com.hsact.domain.utils.centsToDollars
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getAllSettingsUseCase: GetAllSettingsUseCase,
    private val getLastShiftUseCase: GetLastShiftUseCase,
    private val getShiftsInRangeUseCase: GetShiftsInRangeUseCase,
) : ViewModel() {

    val settings: UserSettings = getAllSettingsUseCase.invoke()

    private val _lastShift = MutableStateFlow<Shift?>(null)
    val lastShift: StateFlow<Shift?> = _lastShift

    private val _shiftListThisMonth = MutableStateFlow<List<Shift>>(emptyList())
    val shiftListThisMonth: StateFlow<List<Shift>> = _shiftListThisMonth

    private val _calendarMonth = MutableStateFlow(YearMonth.now())
    val calendarMonth: StateFlow<YearMonth> = _calendarMonth

    private val _calendarShifts = MutableStateFlow<List<Shift>>(emptyList())
    val calendarShifts: StateFlow<List<Shift>> = _calendarShifts

    private val _shiftsForSelection = MutableStateFlow<List<Shift>>(emptyList())
    /**
     * The list of shifts currently selected for the user to choose from (e.g. in a BottomSheet).
     */
    val shiftsForSelection: StateFlow<List<Shift>> = _shiftsForSelection

    private val _chartData = MutableStateFlow(emptyList<Double>())
    val chartData: StateFlow<List<Double>> = _chartData

    private val _goalData = MutableStateFlow(0.0)
    val goalData: StateFlow<Double> = _goalData

    init {
        // Подписка на последнюю смену
        viewModelScope.launch {
            getLastShiftUseCase()
                .collect { last ->
                    _lastShift.value = last
                }
        }
        // Подписка на список смен в этом месяце
        viewModelScope.launch {
            val startOfMonth = YearMonth.now().atDay(1).atStartOfDay()
            val endOfMonth = YearMonth.now().atEndOfMonth().atTime(LocalTime.MAX)

            getShiftsInRangeUseCase(startOfMonth, endOfMonth)
                .collect { list ->
                    _shiftListThisMonth.value = list
                    calculateChart()    // пересчёт графика при каждом изменении
                }
        }
        // Подписка на список смен для календаря (выбранный месяц)
        viewModelScope.launch {
            calendarMonth.collect { month ->
                val start = month.atDay(1).atStartOfDay()
                val end = month.atEndOfMonth().atTime(LocalTime.MAX)
                getShiftsInRangeUseCase(start, end).collect { list ->
                    _calendarShifts.value = list
                }
            }
        }
    }

    fun onPreviousMonth() {
        _calendarMonth.value = _calendarMonth.value.minusMonths(1)
    }

    fun onNextMonth() {
        _calendarMonth.value = _calendarMonth.value.plusMonths(1)
    }

    /**
     * Sets the shifts to be displayed for selection in the BottomSheet.
     */
    fun selectShifts(shifts: List<Shift>) {
        _shiftsForSelection.value = shifts
    }

    fun calculateChart() {
        val shifts = shiftListThisMonth.value

        _goalData.value = settings.goalPerMonth?.toDoubleOrNull() ?: 0.0
        val tempData = mutableMapOf<Int, Double>()
        for (shift in shifts) {
            val day = shift.time.period.start.dayOfMonth
            tempData[day] = (tempData[day] ?: 0.0) + shift.profit.centsToDollars()
        }

        var cumulativeSum = 0.0
        _chartData.value = MutableList(31) { index ->
            val day = index + 1 //day with start at 0
            cumulativeSum += tempData[day] ?: 0.0
            cumulativeSum
        }
    }
}