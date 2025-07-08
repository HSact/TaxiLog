package com.hsact.domain.usecase.settings

import com.hsact.domain.repository.SettingsRepository

class UpdateSettingUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke(key: String, value: Any?) {
        repository.updateSetting(key, value)
    }
}