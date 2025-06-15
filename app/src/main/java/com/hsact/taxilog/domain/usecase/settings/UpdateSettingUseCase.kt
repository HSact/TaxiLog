package com.hsact.taxilog.domain.usecase.settings

import com.hsact.taxilog.data.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(key: String, value: Any?) {
        repository.updateSetting(key, value)
    }
}