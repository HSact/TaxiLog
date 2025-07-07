package com.hsact.taxilog.ui.fragments.shiftDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.Shift
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.domain.usecase.shift.DeleteShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShiftDetailViewModel @Inject constructor(
    private val getAllSettingsUseCase: GetAllSettingsUseCase,
    private val getShiftByIdUseCase: GetShiftByIdUseCase,
    private val deleteShiftUseCase: DeleteShiftUseCase
): ViewModel() {
    private val _shift = MutableStateFlow<Shift?>(null)
    val shift: StateFlow<Shift?> = _shift

    val settings = getAllSettingsUseCase()
    fun loadShift(shiftId: Int) {
        viewModelScope.launch {
            _shift.value = getShiftByIdUseCase(shiftId)
        }
    }
    fun deleteShift() {
        viewModelScope.launch {
            if (_shift.value != null) {
                deleteShiftUseCase(_shift.value!!)
            }
        }
    }
}