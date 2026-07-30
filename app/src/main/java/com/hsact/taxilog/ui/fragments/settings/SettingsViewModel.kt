package com.hsact.taxilog.ui.fragments.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.User
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.auth.GetAuthStateUseCase
import com.hsact.domain.usecase.auth.SignOutUseCase
import com.hsact.domain.usecase.settings.AuthSkippedUseCase
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.domain.usecase.settings.SaveAllSettingsUseCase
import com.hsact.taxilog.ui.locale.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getAllSettingsUseCase: GetAllSettingsUseCase,
    private val saveAllSettingsUseCase: SaveAllSettingsUseCase,
    private val authSkippedUseCase: AuthSkippedUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val signOutUseCase: SignOutUseCase,
    val localeHelper: LocaleHelper
) : ViewModel() {

    private val _settings = MutableStateFlow<UserSettings?>(null)
    
    val user: StateFlow<User?> = getAuthStateUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val settings = getAllSettingsUseCase()
            _settings.value = settings
            updateUiState(settings)
        }
    }

    private fun updateUiState(settings: UserSettings?) {
        val currentSettings = settings ?: UserSettings.default
        _uiState.value = SettingsUiState.Success(currentSettings)
    }

    fun updateSettings(updated: UserSettings) {
        val currentState = _uiState.value
        if (currentState is SettingsUiState.Success) {
            _uiState.value = currentState.copy(settings = updated)
        }
    }

    fun setAuthSkipped(isSkipped: Boolean) {
        viewModelScope.launch {
            authSkippedUseCase.setAuthSkipped(isSkipped)
        }
    }

    fun saveSettings() {
        val currentState = _uiState.value
        if (currentState is SettingsUiState.Success) {
            _uiState.value = currentState.copy(isSaving = true)
            val settingsToSave = currentState.settings.copy(isConfigured = true)
            viewModelScope.launch {
                saveAllSettingsUseCase(settingsToSave)
                localeHelper.setLocale(settingsToSave.language)
                _settings.value = settingsToSave
                _uiState.value = currentState.copy(isSaving = false, settings = settingsToSave)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }
}