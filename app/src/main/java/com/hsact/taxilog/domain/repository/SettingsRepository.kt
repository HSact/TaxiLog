package com.hsact.taxilog.domain.repository

import com.hsact.taxilog.domain.model.UserSettings

interface SettingsRepository {

    val isConfigured: Boolean
    val theme: String?
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

    fun updateSetting(key: String, value: Any)
    fun saveAllSettings(settings: UserSettings)
}