package com.hsact.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.model.settings.currencyNameToSymbolMode
import com.hsact.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : SettingsRepository {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    override val isConfigured: Boolean
        get() = sharedPreferences.getBoolean("Is_configured", false)

    override val authSkipped: Boolean
        get() = sharedPreferences.getBoolean("Auth_skipped", false)

    override val deviceId: String
        get() {
            val current = sharedPreferences.getString("Device_id", null)
            return if (current != null) {
                current
            } else {
                val newId = UUID.randomUUID().toString()
                updateSetting("Device_id", newId)
                newId
            }
        }

    override val theme: String?
        get() = sharedPreferences.getString("Theme", null)

    override val currency: CurrencySymbolMode?
        get() = sharedPreferences.getString("Currency", null).currencyNameToSymbolMode()

    override val language: String?
        get() = sharedPreferences.getString("My_Lang", null)

    override val kmMi: Boolean
        get() = sharedPreferences.getBoolean("KmMi", false)

    override val consumption: String?
        get() = sharedPreferences.getString("Consumption", null)

    override val rented: Boolean
        get() = sharedPreferences.getBoolean("Rented", false)

    override val rentCost: String?
        get() = sharedPreferences.getString("Rent_cost", null)

    override val fuelPrice: String?
        get() = sharedPreferences.getString("Fuel_price", null)

    override val service: Boolean
        get() = sharedPreferences.getBoolean("Service", false)

    override val serviceCost: String?
        get() = sharedPreferences.getString("Service_cost", null)

    override val goalPerMonth: String?
        get() = sharedPreferences.getString("Goal_per_month", null)

    override val schedule: String?
        get() = sharedPreferences.getString("Schedule", null)

    override val taxes: Boolean
        get() = sharedPreferences.getBoolean("Taxes", false)

    override val taxRate: String?
        get() = sharedPreferences.getString("Tax_rate", null)

    override val firstDayOfWeek: Int
        get() = sharedPreferences.getInt("FirstDayOfWeek", 0)

    /**
     * Implementation of [SettingsRepository.getSettingsFlow] using [callbackFlow].
     * Listens for changes in [SharedPreferences] and emits updated [UserSettings].
     */
    override fun getSettingsFlow(): Flow<UserSettings> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(getAllSettings())
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(getAllSettings())
        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override fun getAllSettings(): UserSettings {
        return UserSettings(
            isConfigured = isConfigured,
            language = language,
            theme = theme,
            currency = currency,
            isKmUnit = kmMi,
            consumption = consumption,
            rented = rented,
            rentCost = rentCost,
            service = service,
            serviceCost = serviceCost,
            goalPerMonth = goalPerMonth,
            schedule = schedule,
            taxes = taxes,
            taxRate = taxRate,
            fuelPrice = fuelPrice,
            firstDayOfWeek = firstDayOfWeek
        )
    }

    override fun updateSetting(key: String, value: Any?) {
        sharedPreferences.edit {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                null -> remove(key)
                else -> throw IllegalArgumentException("Unsupported type: ${value.javaClass.name}")
            }
        }
    }

    override fun saveAuthSkipped(isAuthSkipped: Boolean) {
        updateSetting("Auth_skipped", isAuthSkipped)
    }

    override fun saveAllSettings(settings: UserSettings) {
        updateSetting("Is_configured", settings.isConfigured)
        updateSetting("My_Lang", settings.language)
        updateSetting("Theme", settings.theme)
        updateSetting("Currency", settings.currency?.toName())
        updateSetting("KmMi", settings.isKmUnit)
        updateSetting("Consumption", settings.consumption)
        updateSetting("Rented", settings.rented)
        updateSetting("Rent_cost", settings.rentCost)
        updateSetting("Service", settings.service)
        updateSetting("Service_cost", settings.serviceCost)
        updateSetting("Goal_per_month", settings.goalPerMonth)
        updateSetting("Schedule", settings.schedule)
        updateSetting("Taxes", settings.taxes)
        updateSetting("Tax_rate", settings.taxRate)
        updateSetting("Fuel_price", settings.fuelPrice)
        updateSetting("FirstDayOfWeek", settings.firstDayOfWeek)
    }
}