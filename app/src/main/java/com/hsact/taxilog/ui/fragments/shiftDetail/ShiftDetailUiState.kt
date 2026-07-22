package com.hsact.taxilog.ui.fragments.shiftDetail

import com.hsact.domain.model.Shift

/**
 * Represents the UI state for the shift detail screen.
 */
sealed interface ShiftDetailUiState {
    /**
     * Data is being loaded from the database.
     */
    data object Loading : ShiftDetailUiState

    /**
     * Shift data successfully loaded.
     */
    data class Success(val shift: Shift) : ShiftDetailUiState

    /**
     * Shift was not found (e.g. deleted).
     */
    data object NotFound : ShiftDetailUiState
}