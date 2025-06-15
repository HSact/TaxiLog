package com.hsact.taxilog.ui.locale

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

class LocaleProvider(context: Context) {
    private val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
    private val keyLanguage = "My_Lang"

    fun getSavedLanguage(): String {
        return prefs.getString(keyLanguage, "") ?: ""
    }

    fun updateLocale(context: Context, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}