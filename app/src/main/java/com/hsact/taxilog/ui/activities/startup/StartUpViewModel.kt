package com.hsact.taxilog.ui.activities.startup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.data.firebase.ShiftSyncManager
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartUpViewModel @Inject constructor(
    getAllSettingsUseCase: GetAllSettingsUseCase,
    private val syncManager: ShiftSyncManager
): ViewModel() {
    private val _settings = MutableLiveData<UserSettings>()
    val settings: LiveData<UserSettings> = _settings
    init {
        viewModelScope.launch(Dispatchers.IO) {
            syncManager.sync()
            Log.d("StartUpViewModel", "Synced")
        }
        _settings.value = getAllSettingsUseCase()
    }
}