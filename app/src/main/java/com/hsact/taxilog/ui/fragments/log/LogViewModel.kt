package com.hsact.taxilog.ui.fragments.log

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.domain.usecase.shift.DeleteAllShiftsUseCase
import com.hsact.domain.usecase.shift.DeleteShiftUseCase
import com.hsact.domain.usecase.shift.GetAllShiftsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val getAllSettingsUseCase: GetAllSettingsUseCase,
    private val getAllShiftsUseCase: GetAllShiftsUseCase,
    private val deleteShiftUseCase: DeleteShiftUseCase,
    private val deleteAllShiftsUseCase: DeleteAllShiftsUseCase,
) : ViewModel() {

    private val _settings = MutableLiveData<UserSettings>()
    val settings: LiveData<UserSettings> = _settings

    private val _shifts = MutableLiveData<List<Shift>>()
    val shifts: LiveData<List<Shift>> = _shifts

    var recyclerViewState: Parcelable? = null           //For saving the RecyclerView scroll state

    init {
        viewModelScope.launch {
            _settings.value = getAllSettingsUseCase()
        }
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