package com.hsact.taxilog.helpers

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    private const val KEY_LANGUAGE = "My_Lang"

    fun setLocale(context: Context, lang: String): Context {
        saveLanguage(context, lang)
        return updateLocale(context, lang)
    }

    fun updateLocale(context: Context, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun getSavedLanguage(context: Context): String {
        val settings = SettingsHelper.getInstance(context)
        return settings.language ?: Locale.getDefault().language
    }

    private fun saveLanguage(context: Context, lang: String) {
        val settings = SettingsHelper.getInstance(context)
        settings.updateSetting(KEY_LANGUAGE, lang)
    }

    fun getDefault (): String
    {
        val currentLocale: Locale = Locale.getDefault()
        return currentLocale.language
    }
}