package com.example.taxidrivercalculator

import com.example.taxidrivercalculator.DBHelper
import com.example.taxidrivercalculator.Shift

object ShiftHelper {
    fun makeArray(db: DBHelper): MutableList<Shift> {
        val shifts = mutableListOf<Shift>()
        val cursor = db.getShift()


        cursor!!.moveToLast()
        if (cursor.position == -1) {
            cursor.close()
            return mutableListOf()
        }
        cursor.moveToFirst()
        var i = 0
        do {
            shifts.add(Shift(0, "", "", 0.0, 0.0, 0.0, 0.0, 0.0))
            shifts[i].id = cursor.getInt(cursor.getColumnIndex(DBHelper.ID_COL) + 0)
            shifts[i].date = cursor.getString(cursor.getColumnIndex(DBHelper.DATE_COl) + 0)
            shifts[i].time = cursor.getString(cursor.getColumnIndex(DBHelper.TIME_COL) + 0)
            shifts[i].earnings = cursor.getDouble(cursor.getColumnIndex(DBHelper.EARNINGS_COL) + 0)
            shifts[i].wash = cursor.getDouble(cursor.getColumnIndex(DBHelper.WASH_COL) + 0)
            shifts[i].fuelCost = cursor.getDouble(cursor.getColumnIndex(DBHelper.FUEL_COL) + 0)
            shifts[i].mileage = cursor.getDouble(cursor.getColumnIndex(DBHelper.MILEAGE_COL) + 0)
            shifts[i].profit = cursor.getDouble(cursor.getColumnIndex(DBHelper.PROFIT_COL) + 0)
            i++

        } while (cursor.moveToNext())

        cursor.close()
        return shifts
    }

    fun calcAverageEarningsPerHour (shifts: MutableList<Shift>): Double
    {
        var sum = 0.0
        var totalHours = 0.0
        shifts.indices.forEach {
                i -> sum+=shifts[i].earnings
                totalHours+=shifts[i].time.toDouble()
        }
        return sum/totalHours
    }
    fun calcAverageProfitPerHour (shifts: MutableList<Shift>): Double
    {
        var sum = 0.0
        var totalHours = 0.0
        shifts.indices.forEach {
                i -> sum+=shifts[i].profit
            totalHours+=shifts[i].time.toDouble()
        }
        return sum/totalHours
    }
    fun calcAverageShiftDuration (shifts: MutableList<Shift>): Double
    {
        var totalHours = 0.0
        shifts.indices.forEach {
                i -> totalHours+=shifts[i].time.toDouble()
        }
        return totalHours/shifts.size
    }
    fun calcAverageMileage (shifts: MutableList<Shift>): Double
    {
        var totalMileage = 0.0
        shifts.indices.forEach {
                i -> totalMileage+=shifts[i].mileage
        }
        return totalMileage/shifts.size
    }
    fun calcAverageFuelCost (shifts: MutableList<Shift>): Double
    {
        var totalFuelCost = 0.0
        shifts.indices.forEach {
                i -> totalFuelCost+=shifts[i].mileage
        }
        return totalFuelCost/shifts.size
    }
    fun calcTotalShiftDuration (shifts: MutableList<Shift>): Double
    {
        var totalHours = 0.0
        shifts.indices.forEach {
                i -> totalHours+=shifts[i].time.toDouble()
        }
        return totalHours
    }
    fun calcTotalMileage (shifts: MutableList<Shift>): Double
    {
        var totalMileage = 0.0
        shifts.indices.forEach {
                i -> totalMileage+=shifts[i].mileage
        }
        return totalMileage
    }
    fun calcTotalFuelCost (shifts: MutableList<Shift>): Double
    {
        var totalFuel = 0.0
        shifts.indices.forEach {
                i -> totalFuel+=shifts[i].fuelCost
        }
        return totalFuel
    }
    fun calcTotalWash (shifts: MutableList<Shift>): Double
    {
        var totalWash = 0.0
        shifts.indices.forEach {
                i -> totalWash+=shifts[i].wash
        }
        return totalWash
    }
    fun calcTotalEarnings (shifts: MutableList<Shift>): Double
    {
        var totalEarnings = 0.0
        shifts.indices.forEach {
                i -> totalEarnings+=shifts[i].earnings
        }
        return totalEarnings
    }
    fun calcTotalProfit (shifts: MutableList<Shift>): Double
    {
        var totalProfit = 0.0
        shifts.indices.forEach {
                i -> totalProfit+=shifts[i].profit
        }
        return totalProfit
    }

}