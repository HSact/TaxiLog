package com.hsact.taxilog.ui.fragments.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.domain.usecase.shift.GetShiftsInRangeUseCase
import com.hsact.domain.utils.DeprecatedDateFormatter
import com.hsact.domain.utils.averageDuration
import com.hsact.domain.utils.averageEarningsPerHour
import com.hsact.domain.utils.averageFuelCost
import com.hsact.domain.utils.averageMileage
import com.hsact.domain.utils.averageProfit
import com.hsact.domain.utils.averageProfitPerHour
import com.hsact.domain.utils.averageWash
import com.hsact.domain.utils.totalCarExpenses
import com.hsact.domain.utils.totalEarnings
import com.hsact.domain.utils.totalFuelCost
import com.hsact.domain.utils.totalMileage
import com.hsact.domain.utils.totalProfit
import com.hsact.domain.utils.totalService
import com.hsact.domain.utils.totalTax
import com.hsact.domain.utils.totalTime
import com.hsact.domain.utils.totalTips
import com.hsact.domain.utils.totalWash
import com.hsact.taxilog.ui.shift.mappers.centsToCurrency
import com.hsact.taxilog.ui.shift.mappers.metersToKilometers
import com.hsact.taxilog.ui.shift.mappers.minutesToHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getAllSettingsUseCase: GetAllSettingsUseCase,
    private val getShiftsInRangeUseCase: GetShiftsInRangeUseCase,
) : ViewModel() {

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun defineDates() {
        val state = _uiState.value
        if (state.startDate.isNotBlank() && state.endDate.isNotBlank()) {
            return
        }
        val now = LocalDateTime.now()
        val currentDate = now.toLocalDate()
        val firstDayOfMonth = now.toLocalDate().withDayOfMonth(1)
        _uiState.value = _uiState.value.copy(
            startDate = firstDayOfMonth.format(DeprecatedDateFormatter),
            endDate = currentDate.format(DeprecatedDateFormatter),
        )
    }

    fun onBeginDateChange(date: String) {
        _uiState.value = _uiState.value.copy(
            startDate = date
        )
    }

    fun onEndDateChange(date: String) {
        _uiState.value = _uiState.value.copy(
            endDate = date
        )
    }

    fun updateShifts(locale: Locale) {
        val startDate = _uiState.value.startDate
        val endDate = _uiState.value.endDate
        viewModelScope.launch {
            getShiftsInRangeUseCase(
                startDate.localDate.atStartOfDay(),
                endDate.localDate.plusDays(1).atStartOfDay()
            ).collect { list ->
                _shifts.value = list
                val currency = currencySymbol ?: CurrencySymbolMode.fromLocale(locale)
                _uiState.value = buildUiState(_uiState.value.startDate, _uiState.value.endDate,list, locale, currency)
            }
        }
    }

    private fun buildUiState(
        startDate: String,
        endDate: String,
        shiftValue: List<Shift>,
        locale: Locale,
        currency: CurrencySymbolMode
    ): UiState {
        return UiState(
            startDate = startDate,
            endDate = endDate,
            shiftsCount = shiftValue.size.toString(),
            avErPh = shiftValue.averageEarningsPerHour.centsToCurrency(locale, currency),
            avProfitPh = shiftValue.averageProfitPerHour.centsToCurrency(locale, currency),
            avProfit = shiftValue.averageProfit.centsToCurrency(locale, currency),
            avDuration = shiftValue.averageDuration.minutesToHours(locale),
            avMileage = shiftValue.averageMileage.metersToKilometers(locale),
            avFuel = shiftValue.averageFuelCost.centsToCurrency(locale, currency),
            avWash = shiftValue.averageWash.centsToCurrency(locale, currency),
            totalDuration = shiftValue.totalTime.minutesToHours(locale),
            totalMileage = shiftValue.totalMileage.metersToKilometers(locale),
            totalFuel = shiftValue.totalFuelCost.centsToCurrency(locale, currency),
            totalWash = shiftValue.totalWash.centsToCurrency(locale, currency),
            totalService = shiftValue.totalService.centsToCurrency(locale, currency),
            totalEarnings = shiftValue.totalEarnings.centsToCurrency(locale, currency),
            totalProfit = shiftValue.totalProfit.centsToCurrency(locale, currency),
            totalTips = shiftValue.totalTips.centsToCurrency(locale, currency),
            totalTax = shiftValue.totalTax.centsToCurrency(locale, currency),
            totalCarExpenses = shiftValue.totalCarExpenses.centsToCurrency(locale, currency),
        )
    }

    private val currencySymbol: CurrencySymbolMode?
        get() = getAllSettingsUseCase.invoke().currency

    private val String.localDate: LocalDate
        get() = LocalDate.parse(this, DeprecatedDateFormatter)
}