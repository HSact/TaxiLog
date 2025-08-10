package com.hsact.taxilog.ui.locale

import android.content.Context

/**
 * A wrapper for applying a custom locale to a given [Context].
 *
 * If a preferred language is saved in preferences, it will be applied.
 * Otherwise, the context will be returned without modifications.
 *
 * Example usage:
 * ```
 * override fun attachBaseContext(newBase: Context) {
 *     super.attachBaseContext(ContextWrapper.wrapContext(newBase))
 * }
 * ```
 */
internal object ContextWrapper {
    /**
     * Wraps the base context with the preferred locale if it's available.
     * If no preferred locale is set, it simply returns the original context.
     *
     * @param newBase The base context to wrap.
     * @return A context with the preferred locale applied, or the original context if none is set.
     */
    fun wrapContext(newBase: Context): Context {
        val localeProvider = LocaleProvider(newBase)
        val lang = localeProvider.getSavedLanguage()
        return if (lang.isNotEmpty()) {
            localeProvider.updateLocale(newBase, lang)
        } else {
            newBase
        }
    }
}