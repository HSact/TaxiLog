package com.hsact.taxilog.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.taxilog.domain.utils.centsToDollars
import com.hsact.taxilog.domain.model.Shift
import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.taxilog.domain.usecase.shift.GetLastShiftUseCase
import com.hsact.taxilog.domain.usecase.shift.GetShiftsInRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
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

    private val _chartData = MutableStateFlow(emptyList<Double>())
    val chartData: StateFlow<List<Double>> = _chartData

    private val _goalData = MutableStateFlow(0.0)
    val goalData: StateFlow<Double> = _goalData

    init {
        viewModelScope.launch {
            _lastShift.value = getLastShiftUseCase.invoke()
            _shiftListThisMonth.value = getShiftsInRangeUseCase.invoke(
                LocalDateTime.now().withDayOfMonth(1),
                LocalDateTime.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
            )
            calculateChart()
        }
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
        _chartData.value = MutableList(31) { day ->
            cumulativeSum += tempData[day] ?: 0.0
            cumulativeSum
        }
    }
}