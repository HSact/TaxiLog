package com.hsact.domain.usecase.settings

import com.hsact.domain.repository.SettingsRepository

data class GetDeviceIdUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): String {
        return repository.deviceId
    }
}