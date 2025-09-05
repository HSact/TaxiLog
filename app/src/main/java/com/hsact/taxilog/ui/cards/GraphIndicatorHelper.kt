package com.hsact.taxilog.ui.cards

import java.util.Locale
import kotlin.math.roundToLong

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
        val rounded = value.roundToLong()
        return if (rounded >= 1000) {
            val thousands = rounded / 1000.0
            val formatted = if (thousands % 1.0 == 0.0) {
                thousands.toInt().toString()
            } else {
                String.format(locale, "%.1f", thousands)
            }
            val suffix = if (locale.language == "ru") " тыс" else "k"
            "$formatted$suffix"
        } else {
            rounded.toString()
        }
    }
}