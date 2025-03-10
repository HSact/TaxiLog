package com.example.taxidrivercalculator.helpers

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.WeekFields
import java.util.Date
import java.util.Locale

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
    fun calculateDayProgress(currentDate: String = getDayToday(), dbHelper: DBHelper): Double
    {
        val shifts = makeArray(dbHelper)
        if (shifts.isEmpty()) return 0.0
        val shift = shifts.find { it.date == currentDate } ?: return 0.0
        return shift.profit
    }

    fun calculateWeekProgress(currentDate: String = getDayToday(), dbHelper: DBHelper): Double {
        val shifts = makeArray(dbHelper)
        var thisWeekSum = 0.0

        val currentWeek = getCurrentWeek(currentDate)
        val currentYear = LocalDate.now().year
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        for (shift in shifts)
        {
            val thisDate = try
            {
                LocalDate.parse(shift.date, dateFormatter)
            }
            catch (e: DateTimeParseException)
            {
                continue
            }
            val shiftWeek = thisDate.get(WeekFields.of(Locale.getDefault()).weekOfYear())
            if (shiftWeek == currentWeek && thisDate.year == currentYear) {
                thisWeekSum += shift.profit
            }
        }
        return thisWeekSum
    }
    fun calculateMonthProgress(currentDate: String = getDayToday(), dbHelper: DBHelper): Double
    {
        val shifts = makeArray(dbHelper)
        var i = 0
        var thisMonthSum = 0.0
        do {
            val thisDateText = shifts[i].date
            if (thisDateText.indexOf(getCurrentMonth(currentDate)) == 3 && thisDateText.contains(getCurrentYear())) {
                thisMonthSum += shifts[i].profit
            }
            i++
        } while (i < shifts.size)
        return thisMonthSum
    }

    private fun getDayToday(): String
    {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        return LocalDate.now().format(dateFormatter)
    }

    private fun getCurrentWeek(dateReceived: String = ""): Int {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val date = if (dateReceived.isNotEmpty()) {
            try {
                LocalDate.parse(dateReceived, formatter)
            } catch (e: DateTimeParseException) {
                return -1
            }
        } else {
            LocalDate.now()
        }
        return date.get(WeekFields.of(Locale.getDefault()).weekOfYear())
    }

    private fun getCurrentMonth(dateReceived: String = ""): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val thisDate = try {
            dateReceived.takeIf { it.isNotEmpty() }?.let {
                LocalDate.parse(it, dateFormatter)
            } ?: LocalDate.now()
        } catch (e: DateTimeParseException) {
            return "0"
        }
        return thisDate.monthValue.toString().padStart(2, '0')
    }

    private fun getCurrentYear(dateReceived: String = ""): String
    {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val thisDate = try {
            dateReceived.takeIf { it.isNotEmpty() }?.let {
                LocalDate.parse(it, dateFormatter)
            } ?: LocalDate.now()
        } catch (e: DateTimeParseException) {
            return "0"
        }
        return thisDate.year.toString()
    }

    fun calcAverageEarningsPerHour(shift: Shift): Double {
        return if (shift.time.toDouble() > 0.0) centsRound(shift.earnings / shift.time.toDouble()) else 0.0
    }
    fun calcAverageEarningsPerHour (shifts: List<Shift>): Double
    {
        var sum = 0.0
        var totalHours = 0.0
        shifts.indices.forEach {
                i -> sum+=shifts[i].earnings
                totalHours+=shifts[i].time.toDouble()
        }
        return centsRound( sum/totalHours)
    }

    fun calcAverageProfitPerHour (shifts: List<Shift>): Double
    {
        var sum = 0.0
        var totalHours = 0.0
        shifts.indices.forEach {
                i -> sum+=shifts[i].profit
            totalHours+=shifts[i].time.toDouble()
        }
        return centsRound(sum/totalHours)
    }
    fun calcAverageShiftDuration (shifts: List<Shift>): Double
    {
        var totalHours = 0.0
        shifts.indices.forEach {
                i -> totalHours+=shifts[i].time.toDouble()
        }
        return oneRound( totalHours/shifts.size)
    }
    fun calcAverageMileage (shifts: List<Shift>): Double
    {
        var totalMileage = 0.0
        shifts.indices.forEach {
                i -> totalMileage+=shifts[i].mileage
        }
        return oneRound( totalMileage/shifts.size)
    }
    fun calcAverageFuelCost (shifts: List<Shift>): Double
    {
        var totalFuelCost = 0.0
        shifts.indices.forEach {
                i -> totalFuelCost+=shifts[i].mileage
        }
        return centsRound( totalFuelCost/shifts.size)
    }
    fun calcTotalShiftDuration (shifts: List<Shift>): Double
    {
        var totalHours = 0.0
        shifts.indices.forEach {
                i -> totalHours+=shifts[i].time.toDouble()
        }
        return totalHours
    }
    fun calcTotalMileage (shifts: List<Shift>): Double
    {
        var totalMileage = 0.0
        shifts.indices.forEach {
                i -> totalMileage+=shifts[i].mileage
        }
        return totalMileage
    }
    fun calcTotalFuelCost (shifts: List<Shift>): Double
    {
        var totalFuel = 0.0
        shifts.indices.forEach {
                i -> totalFuel+=shifts[i].fuelCost
        }
        return totalFuel
    }
    fun calcTotalWash (shifts: List<Shift>): Double
    {
        var totalWash = 0.0
        shifts.indices.forEach {
                i -> totalWash+=shifts[i].wash
        }
        return totalWash
    }
    fun calcTotalEarnings (shifts: List<Shift>): Double
    {
        var totalEarnings = 0.0
        shifts.indices.forEach {
                i -> totalEarnings+=shifts[i].earnings
        }
        return totalEarnings
    }
    fun calcTotalProfit (shifts: List<Shift>): Double
    {
        var totalProfit = 0.0
        shifts.indices.forEach {
                i -> totalProfit+=shifts[i].profit
        }
        return totalProfit
    }
    fun centsRound (n: Double): Double
    {
        return Math.round(n*100)/100.toDouble()
    }
    private fun oneRound (n: Double): Double
    {
        return Math.round(n*10)/10.toDouble()
    }

    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("H:mm")
        return format.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimeToLong(date: String): Long {
        val df = SimpleDateFormat("H:mm")
        return df.parse(date)!!.time
    }

    fun msToHours (ms: Long) : Double
    {
        return ((ms/60.0/60.0/1000.0)*100.0).toInt()/100.0
    }
}