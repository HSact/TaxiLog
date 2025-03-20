package com.example.taxidrivercalculator.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class SettingsHelper private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    var seted_up: Boolean = false
        private set
    var theme: String = "default"
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

    companion object {
        @Volatile
        private var instance: SettingsHelper? = null

        fun getInstance(context: Context): SettingsHelper {
            return instance ?: synchronized(this) {
                instance ?: SettingsHelper(context).also { it.loadSettings(); instance = it }
            }
        }
    }

    private fun loadSettings() {
        seted_up = sharedPreferences.getBoolean("Seted_up", false)
        //if (!seted_up) return
        theme = sharedPreferences.getString("Theme", "")?: getCurrentTheme()
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
        with(sharedPreferences.edit()) {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
            apply()
        }
        loadSettings()
    }

    private fun getCurrentTheme(): String
    {
        val currentTheme = AppCompatDelegate.getDefaultNightMode()
        if (currentTheme==1)
        {
            return "light"
        }
        if (currentTheme==2)
        {
            return "dark"
        }
        return "default"
    }
}
