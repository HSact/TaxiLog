package com.hsact.taxilog.ui.fragments.log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.taxilog.domain.model.Shift
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

    private val _shifts = MutableLiveData<List<Shift>>()
    val shifts: LiveData<List<Shift>> = _shifts

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
            _shifts.value = getAllShiftsUseCase()
        }
    }

    private fun deleteShift(shift: Shift) {
        viewModelScope.launch {
            deleteShiftUseCase(shift)
            _shifts.value = getAllShiftsUseCase()
        }
    }

    private fun editShift(shift: Shift) {
        //TODO("Not yet implemented")

    }
}