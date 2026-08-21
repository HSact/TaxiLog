package com.hsact.domain.model.settings

import java.util.Locale

enum class CurrencySymbolMode {
    USD,
    EUR,
    RUB,
    GBP,
    JPY,
    ;

    fun toSymbol(): String =
        when (this) {
            USD -> "$"
            EUR -> "€"
            RUB -> "₽"
            GBP -> "£"
            JPY -> "¥"
        }

    val isPrefix: Boolean
        get() = when (this) {
            USD, GBP, JPY -> true
            RUB, EUR -> false
        }

    fun toIndex(): Int =
        when (this) {
            USD -> 0
            EUR -> 1
            RUB -> 2
            GBP -> 3
            JPY -> 4
        }

    fun toName(): String? =
        when (this) {
            USD -> "DOLLAR"
            EUR -> "EURO"
            RUB -> "RUBLE"
            GBP -> "POUND"
            JPY -> "YEN"
        }

    companion object {
        /**
         * Determines the [CurrencySymbolMode] based on the provided [Locale].
         * Checks country code first, then falls back to language code.
         */
        fun fromLocale(locale: Locale): CurrencySymbolMode {
            val country = locale.country.uppercase()
            val language = locale.language.lowercase()

            val byCountry = when (country) {
                "US" -> USD
                "RU" -> RUB
                "GB" -> GBP
                "JP" -> JPY
                "DE", "FR", "ES", "IT" -> EUR
                else -> null
            }

            if (byCountry != null) return byCountry

            // Fallback to language code if country is missing or not matched
            return when (language) {
                "ru" -> RUB
                "ja" -> JPY
                "de", "fr", "es", "it" -> EUR
                else -> USD
            }
        }
    }
}

fun Int.indexToCurrencySymbolMode(): CurrencySymbolMode =
    when (this) {
        0 -> CurrencySymbolMode.USD
        1 -> CurrencySymbolMode.EUR
        2 -> CurrencySymbolMode.RUB
        3 -> CurrencySymbolMode.GBP
        4 -> CurrencySymbolMode.JPY
        else -> CurrencySymbolMode.USD
    }

fun String?.currencyNameToSymbolMode(): CurrencySymbolMode? =
    when (this) {
        "DOLLAR" -> CurrencySymbolMode.USD
        "EURO" -> CurrencySymbolMode.EUR
        "RUBLE" -> CurrencySymbolMode.RUB
        "POUND" -> CurrencySymbolMode.GBP
        "YEN" -> CurrencySymbolMode.JPY
        else -> null
    }
