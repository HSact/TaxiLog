package com.hsact.domain.usecase.settings

import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsFlowUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<UserSettings> {
        return repository.getAllSettingsFlow()
    }
}