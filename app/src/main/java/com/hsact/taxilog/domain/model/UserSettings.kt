package com.hsact.taxilog.domain.model

data class UserSettings(
    val isConfigured: Boolean = false,
    val language: String?,
    val theme: String?,
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
    val fuelPrice: String?
)