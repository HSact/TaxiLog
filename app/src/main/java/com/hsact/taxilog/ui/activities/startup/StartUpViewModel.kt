package com.hsact.taxilog.ui.activities.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.auth.GetAuthStateUseCase
import com.hsact.domain.usecase.settings.AuthSkippedUseCase
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StartUpViewModel @Inject constructor(
    getAllSettingsUseCase: GetAllSettingsUseCase,
    private val authSkippedUseCase: AuthSkippedUseCase,
    getAuthStateUseCase: GetAuthStateUseCase,
): ViewModel() {
    private val _settings = MutableStateFlow<UserSettings?>(null)
    val settings: StateFlow<UserSettings?> = _settings.asStateFlow()

    val authState: StateFlow<AuthState> = getAuthStateUseCase()
        .map { user ->
            if (user != null) AuthState.Authenticated(user)
            else AuthState.NotAuthenticated
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)

    init {
        _settings.value = getAllSettingsUseCase()
    }
    fun isAuthSkipped(): Boolean {
        return authSkippedUseCase.isAuthSkipped()
    }
    fun setAuthSkipped(isSkipped: Boolean) {
        authSkippedUseCase.setAuthSkipped(isSkipped)
    }
}