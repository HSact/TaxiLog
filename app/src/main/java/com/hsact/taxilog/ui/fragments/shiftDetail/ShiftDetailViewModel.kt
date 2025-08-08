package com.hsact.taxilog.ui.fragments.shiftDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.Shift
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.domain.usecase.shift.DeleteShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftByIdUseCase
import com.hsact.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShiftDetailViewModel @Inject constructor(
    private val getAllSettingsUseCase: GetAllSettingsUseCase,
    private val getShiftByIdUseCase: GetShiftByIdUseCase,
    private val deleteShiftUseCase: DeleteShiftUseCase,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
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
        applicationScope.launch {
            try {
                _shift.value?.let { deleteShiftUseCase(it) }
            } catch (e: Exception) {
                Log.e("ShiftDetail", "Error deleting shift", e)
            }
        }
    }
}