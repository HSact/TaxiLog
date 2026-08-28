package com.hsact.taxilog.ui.fragments.log

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetSettingsFlowUseCase
import com.hsact.domain.usecase.shift.DeleteAllShiftsUseCase
import com.hsact.domain.usecase.shift.DeleteShiftUseCase
import com.hsact.domain.usecase.shift.GetAllShiftsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class LogViewModel
    @Inject
    constructor(
        getSettingsFlowUseCase: GetSettingsFlowUseCase,
        private val getAllShiftsUseCase: GetAllShiftsUseCase,
        private val deleteShiftUseCase: DeleteShiftUseCase,
        private val deleteAllShiftsUseCase: DeleteAllShiftsUseCase,
    ) : ViewModel() {
        /**
         * Reactive user settings used for formatting currency and units in the shifts log.
         */
        val settings: StateFlow<UserSettings> =
            getSettingsFlowUseCase()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings.default)

        private val _rawShifts = MutableStateFlow<List<Shift>>(emptyList())

        /**
         * Boolean state indicating if the database has any shifts at all,
         * regardless of current filters or sorting.
         */
        val isDatabaseEmpty: StateFlow<Boolean> =
            _rawShifts.map { it.isEmpty() }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

        /**
         * Current active filter period for the shifts log.
         */
        private val _filterPeriod = MutableStateFlow(LogFilterPeriod.MONTH)
        val filterPeriod: StateFlow<LogFilterPeriod> = _filterPeriod.asStateFlow()

        /**
         * Current active sort order for the shifts log.
         */
        private val _sortOrder = MutableStateFlow(LogSortOrder.DATE_DESC)
        val sortOrder: StateFlow<LogSortOrder> = _sortOrder.asStateFlow()

        /**
         * Final reactive list of shifts after applying filtration and sorting.
         * Also assigns a stable [Shift.sequenceNumber] based on chronological order.
         */
        val shifts: StateFlow<List<Shift>> =
            combine(_rawShifts, _filterPeriod, _sortOrder) { rawList, filter, sort ->
                val listWithSequence =
                    rawList
                        .sortedBy { it.time.period.start }
                        .mapIndexed { index, shift ->
                            shift.copy(sequenceNumber = index + 1)
                        }
                filterAndSort(listWithSequence, filter, sort)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        var recyclerViewState: Parcelable? = null // For saving the RecyclerView scroll state

        init {
            updateList()
        }

        fun handleIntent(intent: LogIntent) {
            when (intent) {
                is LogIntent.UpdateList -> updateList()
                is LogIntent.DeleteShift -> deleteShift(intent.shift)
                is LogIntent.DeleteAllShifts -> deleteAllShifts()
                is LogIntent.ChangeFilter -> _filterPeriod.value = intent.period
                is LogIntent.ChangeSort -> _sortOrder.value = intent.sortOrder
            }
        }

        /**
         * Applies filter and sort logic to the given list of shifts.
         *
         * @param list The original list of shifts.
         * @param filter The filter period to apply.
         * @param sort The sort order to apply.
         * @return A new list containing shifts that match the filter and are sorted correctly.
         */
        private fun filterAndSort(
            list: List<Shift>,
            filter: LogFilterPeriod,
            sort: LogSortOrder,
        ): List<Shift> {
            val now = LocalDateTime.now()
            val filtered =
                when (filter) {
                    LogFilterPeriod.ALL -> list
                    LogFilterPeriod.WEEK ->
                        list.filter {
                            ChronoUnit.DAYS.between(it.time.period.start, now) <= 7
                        }
                    LogFilterPeriod.MONTH ->
                        list.filter {
                            it.time.period.start.month == now.month && it.time.period.start.year == now.year
                        }
                    LogFilterPeriod.YEAR ->
                        list.filter {
                            it.time.period.start.year == now.year
                        }
                }

            return when (sort) {
                LogSortOrder.DATE_DESC -> filtered.sortedByDescending { it.time.period.start }
                LogSortOrder.DATE_ASC -> filtered.sortedBy { it.time.period.start }
                LogSortOrder.PROFIT_DESC -> filtered.sortedByDescending { it.profit }
                LogSortOrder.PROFIT_ASC -> filtered.sortedBy { it.profit }
                LogSortOrder.DURATION_DESC -> filtered.sortedByDescending { it.time.totalDuration }
                LogSortOrder.DURATION_ASC -> filtered.sortedBy { it.time.totalDuration }
            }
        }

        private fun deleteAllShifts() {
            viewModelScope.launch {
                deleteAllShiftsUseCase.invoke()
                _rawShifts.value = getAllShiftsUseCase().first()
            }
        }

        private fun deleteShift(shift: Shift) {
            viewModelScope.launch {
                deleteShiftUseCase(shift)
                _rawShifts.value = getAllShiftsUseCase().first()
            }
        }

        private fun updateList() {
            viewModelScope.launch {
                _rawShifts.value = getAllShiftsUseCase().first()
            }
        }
    }
