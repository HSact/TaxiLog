package com.hsact.taxilog.data.repository

import com.hsact.taxilog.data.db.DBHelper
import com.hsact.taxilog.data.model.Shift
import com.hsact.taxilog.data.utils.DeprecatedDateFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ShiftRepository (private val db: DBHelper) {
    fun getAllShifts(): MutableList<Shift> {
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
        db.close()
        return shifts
    }
    fun getShiftsInDateRange(
        date1: String? = null,
        date2: String? = null,
    ): List<Shift> {
        val formatter = DeprecatedDateFormatter.formatter
        val startDate = date1?.let { LocalDate.parse(it, formatter) }
        val endDate = date2?.let { LocalDate.parse(it, formatter) }
        val shifts = getAllShifts()
        return shifts.filter { shift ->
            val shiftDate = LocalDate.parse(shift.date, formatter)
            (startDate == null || shiftDate >= startDate) &&
                    (endDate == null || shiftDate <= endDate)
        }
    }
    fun addShift(shift: Shift) {
        db.addShift(shift.date, shift.time.toDouble(), shift.earnings,
            shift.wash, shift.fuelCost, shift.mileage, shift.profit)
        db.close()
    }
    fun deleteShift(index: Int) {
        val shifts = getAllShifts()
        shifts.removeAt(index-1)
        db.recreateDB(shifts)
        db.close()
    }
    fun deleteAllShifts()
    {
        db.recreateDB(mutableListOf())
        db.close()
    }
}