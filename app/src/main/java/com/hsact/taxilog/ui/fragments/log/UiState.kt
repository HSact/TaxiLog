package com.hsact.taxilog.ui.fragments.log

import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.UserSettings

data class UiState(
    val shifts: List<Shift> = emptyList(),
    val settings: UserSettings? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showEmptyToast: Boolean = false,
    val showDeleteSuccessToast: Boolean = false,
    val showEditSuccessToast: Boolean = false,
)