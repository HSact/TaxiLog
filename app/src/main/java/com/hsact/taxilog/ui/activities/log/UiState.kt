package com.hsact.taxilog.ui.activities.log

import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.domain.model.UserSettings

data class UiState(
    val shifts: List<ShiftV2> = emptyList(),
    val settings: UserSettings? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showEmptyToast: Boolean = false,
    val showDeleteSuccessToast: Boolean = false,
    val showEditSuccessToast: Boolean = false,
)