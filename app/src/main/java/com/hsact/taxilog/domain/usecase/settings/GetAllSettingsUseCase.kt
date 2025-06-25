package com.hsact.taxilog.domain.usecase.settings

import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): UserSettings {
        return repository.getAllSettings()
    }
}