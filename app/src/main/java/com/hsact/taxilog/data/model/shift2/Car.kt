package com.hsact.taxilog.data.model.shift2

data class Car(
    val id: Int,
    val name: String,
    val rentCost: Long = 0,
    val serviceCost: Long = 0,
    val consumption: Long = 0,
    val totalMileage: Long = 0
)