package com.hsact.taxilog.ui.fragments.settings

import com.hsact.domain.model.settings.UserSettings

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Success(
        val settings: UserSettings,
        val isSaving: Boolean = false,
    ) : SettingsUiState
}
