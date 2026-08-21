package com.hsact.domain.model.settings

data class UserSettings(
    val isConfigured: Boolean = false,
    val language: String?,
    val theme: String?,
    val currency: CurrencySymbolMode?,
    val isKmUnit: Boolean = true,
    val consumption: String?,
    val rented: Boolean = false,
    val rentCost: String?,
    val service: Boolean = false,
    val serviceCost: String?,
    val goalPerMonth: String?,
    val schedule: String?,
    val taxes: Boolean = false,
    val taxRate: String?,
    val fuelPrice: String?,
    val firstDayOfWeek: Int = 0,
) {
    companion object {
        val default = UserSettings(
            language = null,
            theme = null,
            currency = null,
            consumption = "",
            rentCost = "",
            serviceCost = "",
            goalPerMonth = "",
            schedule = "6/1",
            taxRate = "",
            fuelPrice = "",
        )
    }
}
