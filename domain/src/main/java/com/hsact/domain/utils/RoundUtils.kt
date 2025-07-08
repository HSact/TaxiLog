package com.hsact.domain.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun Long.centsToDollars(): Double = (this.toDouble() / 100).round()

fun List<Long>.centsToDollars(): List<Double> {
    return map { it.centsToDollars() }
}

fun Double.toCentsLong(): Long {
    return BigDecimal(this)
        .setScale(2, RoundingMode.HALF_UP)
        .multiply(BigDecimal(100))
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()
}

fun Double.round(decimals: Int = 2): Double {
    return BigDecimal(this)
        .setScale(decimals, RoundingMode.HALF_UP)
        .toDouble()
}