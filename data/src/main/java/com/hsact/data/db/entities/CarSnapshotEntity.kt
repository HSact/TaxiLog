package com.hsact.data.db.entities

data class CarSnapshotEntity(
    val name: String,
    val mileage: Long, //in meters
    val fuelConsumption: Long,  //in milliliters per 100km
    val rentCost: Long, //in cents per shift
    val serviceCost: Long   //in cents per 1000 meters
)