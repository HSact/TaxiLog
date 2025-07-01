package com.hsact.taxilog.domain.model.settings

import java.util.Locale

enum class CurrencySymbolMode {
    USD, RUB, EUR, GBP, CNY, JPY;

    fun toSymbol(): String = when (this) {
        CurrencySymbolMode.USD -> "$"
        CurrencySymbolMode.EUR -> "€"
        CurrencySymbolMode.RUB -> "₽"
        CurrencySymbolMode.GBP -> "£"
        CurrencySymbolMode.CNY -> "¥"
        CurrencySymbolMode.JPY -> "¥"
    }

    fun toIndex(): Int = when (this) {
        USD -> 0
        EUR -> 1
        RUB -> 2
        GBP -> 3
        CNY -> 4
        JPY -> 5
    }

//    fun String.currencyNameToSymbolMode(): CurrencySymbolMode = when (this) {
//        "DOLLAR" -> CurrencySymbolMode.USD
//        "EURO" -> CurrencySymbolMode.EUR
//        "RUBLE" -> CurrencySymbolMode.RUB
//        "POUND" -> CurrencySymbolMode.GBP
//        "YEN" -> CurrencySymbolMode.CNY
//        else -> CurrencySymbolMode.USD
//    }
    fun toName(): String? = when (this) {
        USD -> "DOLLAR"
        EUR -> "EURO"
        RUB -> "RUBLE"
        GBP -> "POUND"
        CNY -> "YEN"
        JPY -> "YEN"

    }
    companion object {
        fun fromLocale(locale: Locale): CurrencySymbolMode {
            return when (locale.country.uppercase()) {
                "US" -> USD
                "RU" -> RUB
                "GB" -> GBP
                "CN" -> CNY
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
    4 -> CurrencySymbolMode.CNY
    5 -> CurrencySymbolMode.JPY
    else -> CurrencySymbolMode.USD
}

fun String?.currencyNameToSymbolMode(): CurrencySymbolMode? = when (this) {
    "DOLLAR" -> CurrencySymbolMode.USD
    "EURO" -> CurrencySymbolMode.EUR
    "RUBLE" -> CurrencySymbolMode.RUB
    "POUND" -> CurrencySymbolMode.GBP
    "YEN" -> CurrencySymbolMode.CNY
    null -> null
    else -> CurrencySymbolMode.USD
}