package com.hsact.taxilog.ui.locale

import android.content.Context

/**
 * [ContextWrapper] is a utility object that "wraps" a [Context] with a custom [Locale],
 * allowing you to apply a different application-wide language without needing to restart
 * the whole app manually.
 *
 * It performs the following steps:
 * 1. Initializes a [LocaleProvider] to retrieve the previously saved application
 *    language from shared preferences.
 * 2. Falls back to the provided [defaultLanguage] if no custom language is set.
 * 3. Invokes [LocaleProvider.updateLocale] to create a new [Context] with the
 *    updated `Resources.configuration`.
 *
 * The returned [Context] can then be safely attached in [Activity.attachBaseContext]
 * to reflect the proper locale from the start of the activity's lifecycle.
 *
 * Example usage:
 * ```
 * override fun attachBaseContext(newBase: Context) {
 *     super.attachBaseContext(ContextWrapper.wrapContext(newBase, "en"))
 * }
 * ```
 * Example usage:
 * ```
 * override fun attachBaseContext(newBase: Context) {
 *      super.attachBaseContext(ContextWrapper.wrapContext(newBase, Locale.getDefault().language))
 *  }
 * ```
 */
object ContextWrapper {
    /**
     * Wraps the given [newBase] context with a new [Context] that uses a custom [Locale].
     *
     * It first checks whether a preferred application language is already saved
     * in [LocaleProvider]. If not, it falls back to [defaultLanguage]. The method then
     * calls [LocaleProvider.updateLocale] to apply this locale to the base context.
     *
     * @param newBase The base context to wrap.
     * @param defaultLanguage The fallback ISO 639-1 code (like "en" or "ru"),
     *                         in case a preferred language is not set.
     * @return A new [Context] instance with the updated locale applied.
     */
    fun wrapContext(newBase: Context, defaultLanguage: String): Context {
        val localeProvider = LocaleProvider(newBase)
        val lang =
            localeProvider.getSavedLanguage().takeIf { it.isNotEmpty() } ?: defaultLanguage
        val context = localeProvider.updateLocale(newBase, lang)
        return context
    }
}