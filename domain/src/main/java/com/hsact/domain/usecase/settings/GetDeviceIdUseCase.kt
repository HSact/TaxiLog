package com.hsact.domain.usecase.settings

import com.hsact.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetDeviceIdUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<String> {
        return repository.deviceId
    }
}