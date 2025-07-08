package com.hsact.domain.model.settings

import java.util.Locale

enum class CurrencySymbolMode {
    USD, EUR, RUB, GBP, JPY;

    fun toSymbol(): String = when (this) {
        USD -> "$"
        EUR -> "€"
        RUB -> "₽"
        GBP -> "£"
        JPY -> "¥"
    }

    fun toIndex(): Int = when (this) {
        USD -> 0
        EUR -> 1
        RUB -> 2
        GBP -> 3
        JPY -> 4
    }

    fun toName(): String? = when (this) {
        USD -> "DOLLAR"
        EUR -> "EURO"
        RUB -> "RUBLE"
        GBP -> "POUND"
        JPY -> "YEN"

    }
    companion object {
        fun fromLocale(locale: Locale): CurrencySymbolMode {
            return when (locale.country.uppercase()) {
                "US" -> USD
                "RU" -> RUB
                "GB" -> GBP
                "JP" -> JPY
                "DE", "FR", "ES", "IT" -> EUR
                else -> USD
            }
        }
    }
}

fun Int.indexToCurrencySymbolMode(): CurrencySymbolMode = when (this) {
    0 -> CurrencySymbolMode.USD
    1 -> CurrencySymbolMode.EUR
    2 -> CurrencySymbolMode.RUB
    3 -> CurrencySymbolMode.GBP
    4 -> CurrencySymbolMode.JPY
    else -> CurrencySymbolMode.USD
}

fun String?.currencyNameToSymbolMode(): CurrencySymbolMode? = when (this) {
    "DOLLAR" -> CurrencySymbolMode.USD
    "EURO" -> CurrencySymbolMode.EUR
    "RUBLE" -> CurrencySymbolMode.RUB
    "POUND" -> CurrencySymbolMode.GBP
    "YEN" -> CurrencySymbolMode.JPY
    else -> null
}