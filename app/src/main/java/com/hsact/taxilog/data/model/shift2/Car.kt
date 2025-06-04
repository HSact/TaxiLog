package com.hsact.taxilog.data.model.shift2

data class Car(
    val id: Int,
    val name: String,
    val rentCost: Double = 0.0,
    val serviceCost: Double = 0.0,
    val consumption: Double = 0.0,
    val totalMileage: Double = 0.0
)