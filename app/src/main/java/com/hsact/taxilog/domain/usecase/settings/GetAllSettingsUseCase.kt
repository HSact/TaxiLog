package com.hsact.taxilog.domain.usecase.settings

import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.data.repository.SettingsRepository
import javax.inject.Inject

class GetAllSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): UserSettings {
        return repository.getAllSettings()
    }
}