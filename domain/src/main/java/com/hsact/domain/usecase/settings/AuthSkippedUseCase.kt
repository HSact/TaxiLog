package com.hsact.domain.usecase.settings

import com.hsact.domain.repository.SettingsRepository

class AuthSkippedUseCase(
    private val repository: SettingsRepository,
) {
    fun isAuthSkipped(): Boolean = repository.authSkipped
    fun setAuthSkipped(value: Boolean) = repository.saveAuthSkipped(value)
}