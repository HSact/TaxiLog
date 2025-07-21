package com.hsact.taxilog.ui.fragments.shiftDetail

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.hsact.data.worker.DeleteShiftWorker
import com.hsact.domain.model.Shift
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.domain.usecase.shift.DeleteShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftByIdUseCase
import com.hsact.taxilog.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @param:ApplicationContext private val context: Context,
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
        val workRequest = OneTimeWorkRequestBuilder<DeleteShiftWorker>()
            .setInputData(workDataOf("remoteId" to _shift.value?.remoteId))
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}