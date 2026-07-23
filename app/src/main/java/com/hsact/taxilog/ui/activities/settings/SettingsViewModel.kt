package com.hsact.taxilog.ui.activities.settings

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
    val settings: StateFlow<UserSettings?> = _settings.asStateFlow()

    val user: StateFlow<User?> = getAuthStateUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadSettings()
    }

    fun setAuthSkipped(isSkipped: Boolean) {
        authSkippedUseCase.setAuthSkipped(isSkipped)
    }

    fun saveSettings(settings: UserSettings) {
        saveAllSettingsUseCase(settings)
        _settings.value = settings
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }

    private fun loadSettings() {
        val result = getAllSettingsUseCase()
        _settings.value = result
    }
}