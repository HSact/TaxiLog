package com.hsact.taxilog.ui.activities.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.taxilog.domain.usecase.settings.SaveAllSettingsUseCase
import com.hsact.taxilog.ui.locale.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getAllSettingsUseCase: GetAllSettingsUseCase,
    private val saveAllSettingsUseCase: SaveAllSettingsUseCase,
    val localeHelper: LocaleHelper
) : ViewModel() {

    private val _settings = MutableLiveData<UserSettings>()
    val settings: LiveData<UserSettings> = _settings

    init {
        loadSettings()
    }

    fun saveSettings(settings: UserSettings) {
        saveAllSettingsUseCase(settings)
        _settings.value = settings
    }

    private fun loadSettings() {
        val result = getAllSettingsUseCase()
        _settings.value = result
    }
}