package com.hsact.domain.usecase.settings

import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.repository.SettingsRepository


class GetAllSettingsUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(): UserSettings {
        return repository.getAllSettings()
    }
}