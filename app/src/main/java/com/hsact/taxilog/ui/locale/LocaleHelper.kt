package com.hsact.taxilog.ui.locale

import android.content.Context
import android.content.res.Configuration
import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.taxilog.domain.usecase.settings.UpdateSettingUseCase
import java.util.Locale
import javax.inject.Inject

class LocaleHelper @Inject constructor(
    getAllSettingsUseCase: GetAllSettingsUseCase,
    private val updateSettingUseCase: UpdateSettingUseCase
) {
    private val settings: UserSettings = getAllSettingsUseCase.invoke()
    private val languageKey = "My_Lang"

    fun setLocale(context: Context, lang: String): Context {
        saveLanguage(lang)
        return updateLocale(context, lang)
    }

    fun updateLocale(context: Context, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun getSavedLanguage(): String {
        return settings.language ?: Locale.getDefault().language
    }

    private fun saveLanguage(lang: String) {
        updateSettingUseCase(languageKey, lang)
    }

    fun getDefault(): String {
        val currentLocale: Locale = Locale.getDefault()
        return currentLocale.language
    }
}