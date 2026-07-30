package com.hsact.taxilog.ui.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.hsact.domain.usecase.settings.UpdateSettingUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocaleHelper @Inject constructor(
    private val updateSettingUseCase: UpdateSettingUseCase
) {
    private val languageKey = "My_Lang"

    fun setLocale(lang: String?, scope: CoroutineScope? = null) {
        scope?.launch {
            updateSettingUseCase(languageKey, lang)
        }
        val appLocales = if (lang != null) {
            LocaleListCompat.forLanguageTags(lang)
        } else {
            LocaleListCompat.getEmptyLocaleList()
        }
        AppCompatDelegate.setApplicationLocales(appLocales)
    }
}