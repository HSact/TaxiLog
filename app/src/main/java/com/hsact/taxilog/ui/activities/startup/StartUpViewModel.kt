package com.hsact.taxilog.ui.activities.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.auth.GetAuthStateUseCase
import com.hsact.domain.usecase.settings.AuthSkippedUseCase
import com.hsact.domain.usecase.settings.GetSettingsFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StartUpViewModel
    @Inject
    constructor(
        getSettingsFlowUseCase: GetSettingsFlowUseCase,
        private val authSkippedUseCase: AuthSkippedUseCase,
        getAuthStateUseCase: GetAuthStateUseCase,
    ) : ViewModel() {
        /**
         * Reactive user settings used for initial app setup (theme, locale).
         */
        val settings: StateFlow<UserSettings> =
            getSettingsFlowUseCase()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings.default)

        val authState: StateFlow<AuthState> =
            getAuthStateUseCase()
                .map { user ->
                    if (user != null) {
                        AuthState.Authenticated(user)
                    } else {
                        AuthState.NotAuthenticated
                    }
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)

        fun isAuthSkipped(): Boolean = authSkippedUseCase.isAuthSkipped()

        fun setAuthSkipped(isSkipped: Boolean) {
            authSkippedUseCase.setAuthSkipped(isSkipped)
        }
    }
