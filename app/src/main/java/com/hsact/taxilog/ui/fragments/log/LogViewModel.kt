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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    getSettingsFlowUseCase: GetSettingsFlowUseCase,
    private val getAllShiftsUseCase: GetAllShiftsUseCase,
    private val deleteShiftUseCase: DeleteShiftUseCase,
    private val deleteAllShiftsUseCase: DeleteAllShiftsUseCase,
) : ViewModel() {

    /**
     * Reactive user settings used for formatting currency and units in the shifts log.
     */
    val settings: StateFlow<UserSettings> = getSettingsFlowUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings.default)

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts.asStateFlow()

    var recyclerViewState: Parcelable? = null           //For saving the RecyclerView scroll state

    init {
        updateList()
    }

    fun handleIntent(intent: LogIntent) {
        when (intent) {
            is LogIntent.UpdateList -> updateList()
            is LogIntent.DeleteShift -> deleteShift(intent.shift)
            is LogIntent.DeleteAllShifts -> deleteAllShifts()
        }
    }

    private fun deleteAllShifts() {
        viewModelScope.launch {
            deleteAllShiftsUseCase.invoke()
            _shifts.value = getAllShiftsUseCase().first()
        }
    }

    private fun deleteShift(shift: Shift) {
        viewModelScope.launch {
            deleteShiftUseCase(shift)
            _shifts.value = getAllShiftsUseCase().first()
        }
    }

    private fun updateList() {
        viewModelScope.launch {
            _shifts.value = getAllShiftsUseCase().first()
        }
    }
}