package com.hsact.taxilog.data.db.entities

data class CarSnapshotEntity(
    val name: String,
    val mileage: Long, //in meters
    val fuelConsumption: Long,  //in milliliters per 100km
    val serviceCost: Long   //in cents per 1000 meters
)