package com.hsact.taxilog.ui.locale

import android.content.Context
import android.content.res.Configuration
import com.hsact.taxilog.domain.usecase.settings.UpdateSettingUseCase
import java.util.Locale
import javax.inject.Inject

class LocaleHelper @Inject constructor(
    private val updateSettingUseCase: UpdateSettingUseCase
) {
    private val languageKey = "My_Lang"

    fun setLocale(context: Context, lang: String): Context {
        updateSettingUseCase(languageKey, lang)
        return updateLocale(context, lang)
    }

    fun updateLocale(context: Context, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}