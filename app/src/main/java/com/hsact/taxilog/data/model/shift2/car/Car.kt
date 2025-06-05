package com.hsact.taxilog.data.model.shift2.car

data class Car(
    val id: Int,
    val name: String,
    val rentCost: Long = 0,
    val serviceCost: Long = 0,
    val consumption: Short = 0,
    val totalMileage: Long = 0,
) {
    init {
        require(consumption >= 0) { "Consumption must be non-negative" }
    }
}