package com.hsact.taxilog.ui.activities.startup

import com.hsact.domain.model.User

sealed interface AuthState {
    object Loading : AuthState

    data class Authenticated(
        val user: User,
    ) : AuthState

    object NotAuthenticated : AuthState
}
