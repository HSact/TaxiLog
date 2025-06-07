package com.hsact.taxilog.data.db.entities

data class CarSnapshotEntity(
    val name: String,
    val startMileage: Long,
    val endMileage: Long,
    val fuelConsumption: Long,
    val serviceCost: Long
)