package com.hsact.domain.repository

import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.model.settings.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val isConfigured: Flow<Boolean>
    val deviceId: Flow<String>
    val authSkipped: Flow<Boolean>
    val theme: Flow<String?>
    val currency: Flow<CurrencySymbolMode?>
    val language: Flow<String?>
    val kmMi: Flow<Boolean>
    val consumption: Flow<String?>
    val rented: Flow<Boolean>
    val rentCost: Flow<String?>
    val fuelPrice: Flow<String?>
    val service: Flow<Boolean>
    val serviceCost: Flow<String?>
    val goalPerMonth: Flow<String?>
    val schedule: Flow<String?>
    val taxes: Flow<Boolean>
    val taxRate: Flow<String?>
    val firstDayOfWeek: Flow<Int>

    fun getAllSettingsFlow(): Flow<UserSettings>
    suspend fun getAllSettings(): UserSettings
    suspend fun updateSetting(key: String, value: Any?)
    suspend fun saveAuthSkipped(isAuthSkipped: Boolean)
    suspend fun saveAllSettings(settings: UserSettings)
}