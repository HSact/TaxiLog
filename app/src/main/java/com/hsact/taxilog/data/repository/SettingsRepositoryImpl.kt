package com.hsact.taxilog.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.hsact.taxilog.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SettingsRepository {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    override val isConfigured: Boolean
        get() = sharedPreferences.getBoolean("Is_configured", false)

    override val theme: String
        get() = sharedPreferences.getString("Theme", "") ?: getCurrentTheme()

    override val language: String?
        get() = sharedPreferences.getString("My_Lang", "")

    override val kmMi: Boolean
        get() = sharedPreferences.getBoolean("KmMi", false)

    override val consumption: String?
        get() = sharedPreferences.getString("Consumption", "")

    override val rented: Boolean
        get() = sharedPreferences.getBoolean("Rented", false)

    override val rentCost: String?
        get() = sharedPreferences.getString("Rent_cost", "")

    override val fuelPrice: String?
        get() = sharedPreferences.getString("Fuel_price", "")

    override val service: Boolean
        get() = sharedPreferences.getBoolean("Service", false)

    override val serviceCost: String?
        get() = sharedPreferences.getString("Service_cost", "")

    override val goalPerMonth: String?
        get() = sharedPreferences.getString("Goal_per_month", "")

    override val schedule: String?
        get() = sharedPreferences.getString("Schedule", "")

    override val taxes: Boolean
        get() = sharedPreferences.getBoolean("Taxes", false)

    override val taxRate: String?
        get() = sharedPreferences.getString("Tax_rate", "")

    override fun updateSetting(key: String, value: Any) {
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