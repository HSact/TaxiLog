package com.hsact.taxilog.domain.shift2.car

data class CarSnapshot(
    val name: String,
    val mileage: Long, //in meters
    val fuelConsumption: Long, //in milliliters per 100km
    val serviceCost: Long = 0, //in cents per 1000 meters
) {
    init {
        require(mileage >= 0) { "Mileage must be non-negative" }
    }
}