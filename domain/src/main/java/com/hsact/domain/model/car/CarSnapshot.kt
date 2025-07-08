package com.hsact.domain.model.car

data class CarSnapshot(
    val name: String = "",
    val mileage: Long, //in meters in this shift
    val fuelConsumption: Long, //in milliliters per 100km
    val rentCost: Long = 0, //in cents per shift
    val serviceCost: Long = 0, //in cents per 1000 meters
) {
    init {
        require(mileage >= 0) { "Mileage must be non-negative" }
    }
}