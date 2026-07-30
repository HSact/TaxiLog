package com.hsact.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.model.settings.currencyNameToSymbolMode
import com.hsact.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    private object PreferencesKeys {
        val IS_CONFIGURED = booleanPreferencesKey("Is_configured")
        val AUTH_SKIPPED = booleanPreferencesKey("Auth_skipped")
        val DEVICE_ID = stringPreferencesKey("Device_id")
        val THEME = stringPreferencesKey("Theme")
        val CURRENCY = stringPreferencesKey("Currency")
        val LANGUAGE = stringPreferencesKey("My_Lang")
        val KM_MI = booleanPreferencesKey("KmMi")
        val CONSUMPTION = stringPreferencesKey("Consumption")
        val RENTED = booleanPreferencesKey("Rented")
        val RENT_COST = stringPreferencesKey("Rent_cost")
        val FUEL_PRICE = stringPreferencesKey("Fuel_price")
        val SERVICE = booleanPreferencesKey("Service")
        val SERVICE_COST = stringPreferencesKey("Service_cost")
        val GOAL_PER_MONTH = stringPreferencesKey("Goal_per_month")
        val SCHEDULE = stringPreferencesKey("Schedule")
        val TAXES = booleanPreferencesKey("Taxes")
        val TAX_RATE = stringPreferencesKey("Tax_rate")
        val FIRST_DAY_OF_WEEK = intPreferencesKey("FirstDayOfWeek")
    }

    override val isConfigured: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.IS_CONFIGURED] ?: false }
    override val authSkipped: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.AUTH_SKIPPED] ?: false }
    
    override val deviceId: Flow<String> = dataStore.data.map { prefs ->
        prefs[PreferencesKeys.DEVICE_ID] ?: ""
    }

    override val theme: Flow<String?> = dataStore.data.map { it[PreferencesKeys.THEME] }
    
    override val currency: Flow<CurrencySymbolMode?> = dataStore.data.map { 
        it[PreferencesKeys.CURRENCY]?.currencyNameToSymbolMode() 
    }
    
    override val language: Flow<String?> = dataStore.data.map { it[PreferencesKeys.LANGUAGE] }
    
    override val kmMi: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.KM_MI] ?: false }
    
    override val consumption: Flow<String?> = dataStore.data.map { it[PreferencesKeys.CONSUMPTION] }
    
    override val rented: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.RENTED] ?: false }
    
    override val rentCost: Flow<String?> = dataStore.data.map { it[PreferencesKeys.RENT_COST] }
    
    override val fuelPrice: Flow<String?> = dataStore.data.map { it[PreferencesKeys.FUEL_PRICE] }
    
    override val service: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.SERVICE] ?: false }
    
    override val serviceCost: Flow<String?> = dataStore.data.map { it[PreferencesKeys.SERVICE_COST] }
    
    override val goalPerMonth: Flow<String?> = dataStore.data.map { it[PreferencesKeys.GOAL_PER_MONTH] }
    
    override val schedule: Flow<String?> = dataStore.data.map { it[PreferencesKeys.SCHEDULE] }
    
    override val taxes: Flow<Boolean> = dataStore.data.map { it[PreferencesKeys.TAXES] ?: false }
    
    override val taxRate: Flow<String?> = dataStore.data.map { it[PreferencesKeys.TAX_RATE] }
    
    override val firstDayOfWeek: Flow<Int> = dataStore.data.map { it[PreferencesKeys.FIRST_DAY_OF_WEEK] ?: 0 }

    override fun getAllSettingsFlow(): Flow<UserSettings> = dataStore.data.map { prefs ->
        mapToUserSettings(prefs)
    }

    override suspend fun getAllSettings(): UserSettings {
        val prefs = dataStore.data.first()
        
        // Ensure Device ID exists
        if (prefs[PreferencesKeys.DEVICE_ID] == null) {
            val newId = UUID.randomUUID().toString()
            dataStore.edit { it[PreferencesKeys.DEVICE_ID] = newId }
        }

        return mapToUserSettings(dataStore.data.first())
    }

    private fun mapToUserSettings(prefs: Preferences): UserSettings {
        return UserSettings(
            isConfigured = prefs[PreferencesKeys.IS_CONFIGURED] ?: false,
            language = prefs[PreferencesKeys.LANGUAGE],
            theme = prefs[PreferencesKeys.THEME],
            currency = prefs[PreferencesKeys.CURRENCY]?.currencyNameToSymbolMode(),
            isKmUnit = prefs[PreferencesKeys.KM_MI] ?: true,
            consumption = prefs[PreferencesKeys.CONSUMPTION],
            rented = prefs[PreferencesKeys.RENTED] ?: false,
            rentCost = prefs[PreferencesKeys.RENT_COST],
            service = prefs[PreferencesKeys.SERVICE] ?: false,
            serviceCost = prefs[PreferencesKeys.SERVICE_COST],
            goalPerMonth = prefs[PreferencesKeys.GOAL_PER_MONTH],
            schedule = prefs[PreferencesKeys.SCHEDULE],
            taxes = prefs[PreferencesKeys.TAXES] ?: false,
            taxRate = prefs[PreferencesKeys.TAX_RATE],
            fuelPrice = prefs[PreferencesKeys.FUEL_PRICE],
            firstDayOfWeek = prefs[PreferencesKeys.FIRST_DAY_OF_WEEK] ?: 0
        )
    }

    override suspend fun updateSetting(key: String, value: Any?) {
        dataStore.edit { prefs ->
            when (value) {
                is Boolean -> prefs[booleanPreferencesKey(key)] = value
                is String -> prefs[stringPreferencesKey(key)] = value
                is Int -> prefs[intPreferencesKey(key)] = value
                null -> {
                    // We don't know the type of the key if value is null, 
                    // so we try to remove it from all common types or just use a generic remove if possible.
                    // DataStore requires a typed key to remove.
                    // This is a bit tricky. We can try to remove all possible keys with this name.
                    prefs.remove(booleanPreferencesKey(key))
                    prefs.remove(stringPreferencesKey(key))
                    prefs.remove(intPreferencesKey(key))
                }
                else -> throw IllegalArgumentException("Unsupported type: ${value.javaClass.name}")
            }
        }
    }

    override suspend fun saveAuthSkipped(isAuthSkipped: Boolean) {
        dataStore.edit { it[PreferencesKeys.AUTH_SKIPPED] = isAuthSkipped }
    }

    override suspend fun saveAllSettings(settings: UserSettings) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_CONFIGURED] = settings.isConfigured
            settings.language?.let { prefs[PreferencesKeys.LANGUAGE] = it } ?: prefs.remove(PreferencesKeys.LANGUAGE)
            settings.theme?.let { prefs[PreferencesKeys.THEME] = it } ?: prefs.remove(PreferencesKeys.THEME)
            settings.currency?.toName()?.let { prefs[PreferencesKeys.CURRENCY] = it } ?: prefs.remove(PreferencesKeys.CURRENCY)
            prefs[PreferencesKeys.KM_MI] = settings.isKmUnit
            settings.consumption?.let { prefs[PreferencesKeys.CONSUMPTION] = it } ?: prefs.remove(PreferencesKeys.CONSUMPTION)
            prefs[PreferencesKeys.RENTED] = settings.rented
            settings.rentCost?.let { prefs[PreferencesKeys.RENT_COST] = it } ?: prefs.remove(PreferencesKeys.RENT_COST)
            prefs[PreferencesKeys.SERVICE] = settings.service
            settings.serviceCost?.let { prefs[PreferencesKeys.SERVICE_COST] = it } ?: prefs.remove(PreferencesKeys.SERVICE_COST)
            settings.goalPerMonth?.let { prefs[PreferencesKeys.GOAL_PER_MONTH] = it } ?: prefs.remove(PreferencesKeys.GOAL_PER_MONTH)
            settings.schedule?.let { prefs[PreferencesKeys.SCHEDULE] = it } ?: prefs.remove(PreferencesKeys.SCHEDULE)
            prefs[PreferencesKeys.TAXES] = settings.taxes
            settings.taxRate?.let { prefs[PreferencesKeys.TAX_RATE] = it } ?: prefs.remove(PreferencesKeys.TAX_RATE)
            settings.fuelPrice?.let { prefs[PreferencesKeys.FUEL_PRICE] = it } ?: prefs.remove(PreferencesKeys.FUEL_PRICE)
            prefs[PreferencesKeys.FIRST_DAY_OF_WEEK] = settings.firstDayOfWeek
        }
    }
}