package com.hsact.taxilog.domain.shift2.car

data class CarSnapshot(
    val name: String,
    val startMileage: Long,
    val endMileage: Long,
    val fuelConsumption: Long,
    val serviceCost: Long = 0,
)