package com.hsact.taxilog.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    var isConfigured: Boolean = false
        private set
    var theme: String = "default"
        private set
    var language: String? = null
        private set
    var kmMi: Boolean = false
        private set
    var consumption: String? = null
        private set
    var rented: Boolean = false
        private set
    var rentCost: String? = null
        private set
    var fuelPrice: String? = null
        private set
    var service: Boolean = false
        private set
    var serviceCost: String? = null
        private set
    var goalPerMonth: String? = null
        private set
    var schedule: String? = null
        private set
    var taxes: Boolean = false
        private set
    var taxRate: String? = null
        private set

    private fun loadSettings() {
        isConfigured = sharedPreferences.getBoolean("Seted_up", false)
        theme = sharedPreferences.getString("Theme", "") ?: getCurrentTheme()
        language = sharedPreferences.getString("My_Lang", "")
        kmMi = sharedPreferences.getBoolean("KmMi", false)
        consumption = sharedPreferences.getString("Consumption", "")
        rented = sharedPreferences.getBoolean("Rented", false)
        rentCost = sharedPreferences.getString("Rent_cost", "")
        fuelPrice = sharedPreferences.getString("Fuel_price", "")
        service = sharedPreferences.getBoolean("Service", false)
        serviceCost = sharedPreferences.getString("Service_cost", "")
        goalPerMonth = sharedPreferences.getString("Goal_per_month", "")
        schedule = sharedPreferences.getString("Schedule", "")
        taxes = sharedPreferences.getBoolean("Taxes", false)
        taxRate = sharedPreferences.getString("Tax_rate", "")
    }

    fun updateSetting(key: String, value: Any) {
        sharedPreferences.edit {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
        loadSettings()
    }

    private fun getCurrentTheme(): String {
        val currentTheme = AppCompatDelegate.getDefaultNightMode()
        if (currentTheme == 1) {
            return "light"
        }
        if (currentTheme == 2) {
            return "dark"
        }
        return "default"
    }
}