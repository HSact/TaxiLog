package com.hsact.taxilog.domain.usecase.settings

import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.data.repository.SettingsRepository
import javax.inject.Inject

class SaveAllSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(settings: UserSettings) {
        return repository.saveAllSettings(settings)
    }
}