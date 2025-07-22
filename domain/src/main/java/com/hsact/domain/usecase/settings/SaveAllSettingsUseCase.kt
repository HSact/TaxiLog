package com.hsact.domain.usecase.settings

import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.repository.SettingsRepository

class SaveAllSettingsUseCase(
    private val repository: SettingsRepository,
) {
    operator fun invoke(settings: UserSettings) {
        return repository.saveAllSettings(settings)
    }
}