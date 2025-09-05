package com.hsact.taxilog.ui.cards

import java.util.Locale

internal object GraphIndicatorHelper {
    fun buildIndicators(
        min: Double,
        max: Double,
    ): List<Double> {
        if (min >= max) return listOf(min, min, min, min, min)
        val step = (max - min) / 4
        return List(5) { i -> min + step * i }.reversed()
    }

    fun formatIndicatorValue(value: Double, locale: Locale): String {
        val rounded = value.toLong()
        val suffix = if (locale.language == "ru") " 000" else "k"
        return if (rounded >= 5000) {
            "${rounded / 1000}${suffix}"
        } else {
            rounded.toString()
        }
    }
}