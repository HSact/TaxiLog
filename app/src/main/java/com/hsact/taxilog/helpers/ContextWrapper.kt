package com.hsact.taxilog.helpers

import android.content.Context

object ContextWrapper {
    fun wrapContext(newBase: Context, defaultLanguage: String): Context {
        val localeProvider = LocaleProvider(newBase)
        val lang =
            localeProvider.getSavedLanguage().takeIf { it.isNotEmpty() } ?: defaultLanguage
        val context = localeProvider.updateLocale(newBase, lang)
        return context
    }
}