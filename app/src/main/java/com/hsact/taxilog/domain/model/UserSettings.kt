package com.hsact.taxilog.domain.model

data class UserSettings(
    val isConfigured: Boolean,
    val lang: String,
    val theme: String,
    val kmMi: Boolean,
    val consumption: String,
    val rented: Boolean,
    val rentCost: String,
    val service: Boolean,
    val serviceCost: String,
    val goalPerMonth: String,
    val schedule: String,
    val taxes: Boolean,
    val taxRate: String,
    val fuelPrice: String
)