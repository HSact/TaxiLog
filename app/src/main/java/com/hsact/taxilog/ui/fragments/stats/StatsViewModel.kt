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
import com.hsact.domain.utils.averageProfitPerHour
import com.hsact.domain.utils.averageWash
import com.hsact.domain.utils.totalEarnings
import com.hsact.domain.utils.totalFuelCost
import com.hsact.domain.utils.totalMileage
import com.hsact.domain.utils.totalProfit
import com.hsact.domain.utils.totalTime
import com.hsact.domain.utils.totalWash
import com.hsact.taxilog.ui.shift.mappers.centsToCurrency
import com.hsact.taxilog.ui.shift.mappers.metersToKilometers
import com.hsact.taxilog.ui.shift.mappers.minutesToHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import java.util.Locale

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getAllSettingsUseCase: GetAllSettingsUseCase,
    private val getShiftsInRangeUseCase: GetShiftsInRangeUseCase,
) : ViewModel() {
    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: MutableStateFlow<List<Shift>> = _shifts

    private val _uiState = MutableStateFlow(UiState())
    val uiState: MutableStateFlow<UiState> = _uiState

    private val now = LocalDateTime.now()
    private val currentDate = now.toLocalDate()
    private val firstDayOfMonth = now.toLocalDate().withDayOfMonth(1)
    var startDate: String = firstDayOfMonth.format(DeprecatedDateFormatter)
    var endDate: String = currentDate.format(DeprecatedDateFormatter)

    fun defineDates() {
        val now = LocalDateTime.now()
        val currentDate = now.toLocalDate()
        val firstDayOfMonth = now.toLocalDate().withDayOfMonth(1)
        startDate = firstDayOfMonth.format(DeprecatedDateFormatter)
        endDate = currentDate.format(DeprecatedDateFormatter)
    }

    fun updateShifts(locale: Locale) {
        viewModelScope.launch {
            _shifts.value = getShiftsInRangeUseCase.invoke(
                startDate.toLocalDate().atStartOfDay(),
                endDate.toLocalDate().plusDays(1).atStartOfDay()
            )
            val currency = getCurrencySymbol()?: CurrencySymbolMode.fromLocale(locale)
            val shiftValue = _shifts.value
            _uiState.value = UiState(
                shiftsCount = shiftValue.size.toString(),
                avErPh = shiftValue.averageEarningsPerHour.centsToCurrency(locale, currency),
                avProfitPh = shiftValue.averageProfitPerHour.centsToCurrency(locale, currency),
                avDuration = shiftValue.averageDuration.minutesToHours(locale),
                avMileage = shiftValue.averageMileage.metersToKilometers(locale),
                avFuel = shiftValue.averageFuelCost.centsToCurrency(locale, currency),
                avWash = shiftValue.averageWash.centsToCurrency(locale, currency),
                totalDuration = shiftValue.totalTime.minutesToHours(locale),
                totalMileage = shiftValue.totalMileage.metersToKilometers(locale),
                totalFuel = shiftValue.totalFuelCost.centsToCurrency(locale, currency),
                totalWash = shiftValue.totalWash.centsToCurrency(locale, currency),
                totalEarnings = shiftValue.totalEarnings.centsToCurrency(locale, currency),
                totalProfit = shiftValue.totalProfit.centsToCurrency(locale, currency),
            )
        }
    }

    private fun getCurrencySymbol(): CurrencySymbolMode? {
        return getAllSettingsUseCase.invoke().currency
    }

    private fun String.toLocalDate(): LocalDate {
        return LocalDate.parse(this, DeprecatedDateFormatter)
    }
}