package com.hsact.taxilog.utils

/**
 * Utility for formatting and validating numeric input strings in real-time.
 * Ensures the input is a valid positive decimal number with limited precision.
 */
object NumericInputUtils {

    /**
     * Filters and formats the input string based on numeric constraints.
     *
     * @param input The raw input string from the user.
     * @param maxLength The maximum allowed length of the input string.
     * @param maxValue The maximum allowed numeric value.
     * @param maxDecimalPlaces The maximum number of digits allowed in the fractional part.
     * @return The formatted string if valid, or null if the input should be rejected.
     */
    fun formatNumericInput(
        input: String,
        maxLength: Int = 10,
        maxValue: Double = Double.MAX_VALUE,
        maxDecimalPlaces: Int = 2
    ): String? {
        if (input.isEmpty()) return ""
        if (input.length > maxLength) return null

        val allowedChars = "0123456789.,"
        if (input.any { it !in allowedChars }) return null

        val separatorCount = input.count { it == '.' || it == ',' }
        if (separatorCount > 1) return null

        // Leading zero logic (allow "0." but not "05")
        if (input.startsWith("0") && input.length > 1) {
            val nextChar = input[1]
            if (nextChar != '.' && nextChar != ',') return null
        }

        // Precision check
        val separatorIndex = input.indexOfAny(charArrayOf('.', ','))
        if (separatorIndex != -1) {
            val fractionalPart = input.substring(separatorIndex + 1)
            if (fractionalPart.length > maxDecimalPlaces) return null
        }

        val numericValue = input.replace(',', '.').toDoubleOrNull()
        if (numericValue != null && numericValue > maxValue) return null

        return input
    }
}
