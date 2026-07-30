package com.hsact.taxilog.ui.fragments.shiftDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.di.ApplicationScope
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetSettingsFlowUseCase
import com.hsact.domain.usecase.shift.DeleteShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftByIdUseCase
import com.hsact.domain.usecase.shift.GetShiftSequenceNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShiftDetailViewModel
    @Inject
    constructor(
        getSettingsFlowUseCase: GetSettingsFlowUseCase,
        private val getShiftByIdUseCase: GetShiftByIdUseCase,
        private val getShiftSequenceNumberUseCase: GetShiftSequenceNumberUseCase,
        private val deleteShiftUseCase: DeleteShiftUseCase,
        @param:ApplicationScope private val applicationScope: CoroutineScope,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<ShiftDetailUiState>(ShiftDetailUiState.Loading)

        /**
         * The reactive UI state of the shift detail screen.
         */
        val uiState: StateFlow<ShiftDetailUiState> = _uiState

        private val _sequenceNumber = MutableStateFlow<Int?>(null)

        /**
         * The reactive sequence number of the shift for display to the user.
         */
        val sequenceNumber: StateFlow<Int?> = _sequenceNumber

        /**
         * Reactive user settings for formatting currency and time in shift details.
         */
        val settings: StateFlow<UserSettings> =
            getSettingsFlowUseCase()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings.default)

        /**
         * Loads the shift data and its sequence number reactively.
         * @param shiftId The technical ID of the shift to load.
         */
        fun loadShift(shiftId: Int) {
            viewModelScope.launch {
                getShiftSequenceNumberUseCase(shiftId).collect { number ->
                    _sequenceNumber.value = number
                }
            }

            viewModelScope.launch {
                getShiftByIdUseCase(shiftId).collect { shift ->
                    _uiState.value =
                        if (shift != null) {
                            ShiftDetailUiState.Success(shift)
                        } else {
                            ShiftDetailUiState.NotFound
                        }
                }
            }
        }

        fun deleteShift() {
            applicationScope.launch {
                try {
                    val currentShift = (uiState.value as? ShiftDetailUiState.Success)?.shift
                    currentShift?.let { deleteShiftUseCase(it) }
                } catch (e: Exception) {
                    Log.e("ShiftDetail", "Error deleting shift", e)
                }
            }
        }
    }
