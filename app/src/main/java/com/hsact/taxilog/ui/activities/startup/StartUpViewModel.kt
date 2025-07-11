package com.hsact.taxilog.ui.activities.startup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartUpViewModel @Inject constructor(
    getAllSettingsUseCase: GetAllSettingsUseCase,
): ViewModel() {
    private val _settings = MutableLiveData<UserSettings>()
    val settings: LiveData<UserSettings> = _settings
    init {
        _settings.value = getAllSettingsUseCase()
    }
}