package com.hsact.taxilog.domain.usecase.settings

import com.hsact.taxilog.domain.model.settings.UserSettings
import com.hsact.taxilog.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveAllSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(settings: UserSettings) {
        return repository.saveAllSettings(settings)
    }
}