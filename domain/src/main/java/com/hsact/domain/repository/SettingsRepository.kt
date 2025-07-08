package com.hsact.domain.repository

import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.model.settings.UserSettings

interface SettingsRepository {

    val isConfigured: Boolean
    val deviceId: String
    val theme: String?
    val currency: CurrencySymbolMode?
    val language: String?
    val kmMi: Boolean
    val consumption: String?
    val rented: Boolean
    val rentCost: String?
    val fuelPrice: String?
    val service: Boolean
    val serviceCost: String?
    val goalPerMonth: String?
    val schedule: String?
    val taxes: Boolean
    val taxRate: String?

    fun getAllSettings(): UserSettings
    fun updateSetting(key: String, value: Any?)
    fun saveAllSettings(settings: UserSettings)
}