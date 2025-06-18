package com.hsact.taxilog.ui.activities.log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.taxilog.domain.usecase.shift.DeleteAllShiftsUseCase
import com.hsact.taxilog.domain.usecase.shift.DeleteShiftUseCase
import com.hsact.taxilog.domain.usecase.shift.GetAllShiftsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val getAllSettingsUseCase: GetAllSettingsUseCase,
    private val getAllShiftsUseCase: GetAllShiftsUseCase,
    private val deleteShiftUseCase: DeleteShiftUseCase,
    private val deleteAllShiftsUseCase: DeleteAllShiftsUseCase
): ViewModel() {

    private val _settings = MutableLiveData<UserSettings>()
    val settings: LiveData<UserSettings> = _settings

    private val _shifts = MutableLiveData<List<ShiftV2>>()
    val shifts: LiveData<List<ShiftV2>> = _shifts

    init {
        viewModelScope.launch {
        _settings.value = getAllSettingsUseCase()
        _shifts.value = getAllShiftsUseCase()
        }
    }

    fun handleIntent(intent: LogIntent) {
        when (intent) {
            //is LogIntent.LoadShifts -> loadShifts()
            is LogIntent.EditShift -> editShift(intent.shift)
            is LogIntent.DeleteShift -> deleteShift(intent.shift)
            is LogIntent.DeleteAllShifts -> deleteAllShifts()
        }

    }

    private fun deleteAllShifts() {
        viewModelScope.launch {
            deleteAllShiftsUseCase.invoke()
        }
    }

    private fun deleteShift(shiftV2: ShiftV2) {
        viewModelScope.launch {
            deleteShiftUseCase(shiftV2)
        }
    }

    private fun editShift(shift: ShiftV2) {
        TODO("Not yet implemented")
    }
}