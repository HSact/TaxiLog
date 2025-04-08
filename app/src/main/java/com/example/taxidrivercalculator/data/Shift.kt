package com.example.taxidrivercalculator.data

data class Shift(
    var id: Int,
    var date: String,
    var time: String,
    var earnings: Double,
    var wash: Double,
    var fuelCost: Double,
    var mileage: Double,
    var profit: Double
)
{
    fun isValid(): Boolean {
        return earnings >= 0.0 &&
                wash >= 0.0 &&
                fuelCost >= 0.0 &&
                mileage >= 0.0 &&
                profit >= 0.0 &&
                isValidDate(date) &&
                isValidTime(time)
    }

    private fun isValidDate(date: String): Boolean
    {
        return date.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}"))
    }

    private fun isValidTime(time: String): Boolean
    {
        return (time.toDouble()>0.0)
    }
}