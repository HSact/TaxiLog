package com.hsact.taxilog.ui.fragments.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.taxilog.data.model.Shift
import com.hsact.taxilog.data.utils.DeprecatedDateFormatter
import com.hsact.taxilog.data.utils.ShiftStatsUtil
import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.domain.usecase.shift.GetShiftsInRangeUseCase
import com.hsact.taxilog.domain.utils.toLegacy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getShiftsInRangeUseCase: GetShiftsInRangeUseCase,
) : ViewModel() {
    private val _shifts = MutableStateFlow<List<ShiftV2>>(emptyList())
    val shifts: MutableStateFlow<List<ShiftV2>> = _shifts

    val shiftsLegacy: List<Shift>
        get() = shifts.value.toLegacy

    private val now = LocalDateTime.now()
    private val currentDate = now.toLocalDate()
    private val firstDayOfMonth = now.toLocalDate().withDayOfMonth(1)
    var startDate: String = firstDayOfMonth.format(DeprecatedDateFormatter)
    var endDate: String = currentDate.format(DeprecatedDateFormatter)

    init {
        updateShifts()
    }

    val shiftsCount: String get() = shiftsLegacy.size.toString()
    val avErPh: String get() = ShiftStatsUtil.calcAverageEarningsPerHour(shiftsLegacy).toString()
    val avProfitPh: String get() = ShiftStatsUtil.calcAverageProfitPerHour(shiftsLegacy).toString()
    val avDuration: String get() = ShiftStatsUtil.calcAverageShiftDuration(shiftsLegacy).toString()
    val avMileage: String get() = ShiftStatsUtil.calcAverageMileage(shiftsLegacy).toString()
    val totalDuration: String get() = ShiftStatsUtil.calcTotalShiftDuration(shiftsLegacy).toString()
    val totalMileage: String get() = ShiftStatsUtil.calcTotalMileage(shiftsLegacy).toString()
    val totalWash: String get() = ShiftStatsUtil.calcTotalWash(shiftsLegacy).toString()
    val totalEarnings: String get() = ShiftStatsUtil.calcTotalEarnings(shiftsLegacy).toString()
    val totalProfit: String get() = ShiftStatsUtil.calcTotalProfit(shiftsLegacy).toString()
    val avFuel: String get() = ShiftStatsUtil.calcAverageFuelCost(shiftsLegacy).toString()
    val totalFuel: String get() = ShiftStatsUtil.calcTotalFuelCost(shiftsLegacy).toString()

    fun defineDates() {
        val now = LocalDateTime.now()
        val currentDate = now.toLocalDate()
        val firstDayOfMonth = now.toLocalDate().withDayOfMonth(1)
        startDate = firstDayOfMonth.format(DeprecatedDateFormatter)
        endDate = currentDate.format(DeprecatedDateFormatter)
    }

    fun updateShifts() {
        viewModelScope.launch {
            _shifts.value = getShiftsInRangeUseCase.invoke(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().plusDays(1).atStartOfDay()
            )
        }
    }

    private fun String.toLocalDate(): LocalDate {
        return LocalDate.parse(this, DeprecatedDateFormatter)
    }
}