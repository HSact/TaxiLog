package com.example.taxidrivercalculator.helpers

import android.annotation.SuppressLint
import com.example.taxidrivercalculator.data.model.Shift
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.WeekFields
import java.util.Date
import java.util.Locale

object ShiftHelper {

    fun calculateDayProgress(currentDate: String = getDayToday(), shifts: List<Shift>): Double {
        if (shifts.isEmpty()) return 0.0
        val shift = shifts.find { it.date == currentDate } ?: return 0.0
        return shift.profit
    }

    fun calculateWeekProgress(currentDate: String = getDayToday(), shifts: List<Shift>): Double {
        if (shifts.isEmpty()) return 0.0
        var thisWeekSum = 0.0

        val currentWeek = getCurrentWeek(currentDate)
        val currentYear = LocalDate.now().year
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        for (shift in shifts) {
            val thisDate = try {
                LocalDate.parse(shift.date, dateFormatter)
            } catch (e: DateTimeParseException) {
                continue
            }
            val shiftWeek = thisDate.get(WeekFields.of(Locale.getDefault()).weekOfYear())
            if (shiftWeek == currentWeek && thisDate.year == currentYear) {
                thisWeekSum += shift.profit
            }
        }
        return thisWeekSum
    }

    fun calculateMonthProgress(currentDate: String = getDayToday(), shifts: List<Shift>): Double {
        if (shifts.isEmpty()) return 0.0
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val current = LocalDate.parse(currentDate, formatter)
        var thisMonthSum = 0.0
        for (shift in shifts) {
            val shiftDate = LocalDate.parse(shift.date, formatter)
            if (shiftDate.month == current.month && shiftDate.year == current.year) {
                thisMonthSum += shift.profit
            }
        }
        return thisMonthSum
    }

    fun filterShiftsByDatePeriod(
        shifts: List<Shift>,
        date1: String? = null,
        date2: String? = null,
    ): List<Shift> {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val startDate = date1?.let { LocalDate.parse(it, formatter) }
        val endDate = date2?.let { LocalDate.parse(it, formatter) }

        return shifts.filter { shift ->
            val shiftDate = LocalDate.parse(shift.date, formatter)
            (startDate == null || shiftDate >= startDate) &&
                    (endDate == null || shiftDate <= endDate)
        }
    }

    private fun getDayToday(): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        return LocalDate.now().format(dateFormatter)
    }

    fun getCurrentDay(dateReceived: String = ""): Int {
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
        return date.dayOfMonth - 1
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

    fun getCurrentMonth(dateReceived: String = ""): String {
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

    private fun getCurrentYear(dateReceived: String = ""): String {
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

    fun calcAverageEarningsPerHour(shifts: List<Shift>): Double {
        var sum = 0.0
        var totalHours = 0.0
        shifts.indices.forEach { i ->
            sum += shifts[i].earnings
            totalHours += shifts[i].time.toDouble()
        }
        return centsRound(sum / totalHours)
    }

    fun calcAverageProfitPerHour(shifts: List<Shift>): Double {
        var sum = 0.0
        var totalHours = 0.0
        shifts.indices.forEach { i ->
            sum += shifts[i].profit
            totalHours += shifts[i].time.toDouble()
        }
        return centsRound(sum / totalHours)
    }

    fun calcAverageShiftDuration(shifts: List<Shift>): Double {
        var totalHours = 0.0
        shifts.indices.forEach { i ->
            totalHours += shifts[i].time.toDouble()
        }
        return oneRound(totalHours / shifts.size)
    }

    fun calcAverageMileage(shifts: List<Shift>): Double {
        var totalMileage = 0.0
        shifts.indices.forEach { i ->
            totalMileage += shifts[i].mileage
        }
        return oneRound(totalMileage / shifts.size)
    }

    fun calcAverageFuelCost(shifts: List<Shift>): Double {
        var totalFuelCost = 0.0
        shifts.indices.forEach { i ->
            totalFuelCost += shifts[i].fuelCost
        }
        return centsRound(totalFuelCost / shifts.size)
    }

    fun calcTotalShiftDuration(shifts: List<Shift>): Double {
        var totalHours = 0.0
        shifts.indices.forEach { i ->
            totalHours += shifts[i].time.toDouble()
        }
        return totalHours
    }

    fun calcTotalMileage(shifts: List<Shift>): Double {
        var totalMileage = 0.0
        shifts.indices.forEach { i ->
            totalMileage += shifts[i].mileage
        }
        return totalMileage
    }

    fun calcTotalFuelCost(shifts: List<Shift>): Double {
        var totalFuel = 0.0
        shifts.indices.forEach { i ->
            totalFuel += shifts[i].fuelCost
        }
        return totalFuel
    }

    fun calcTotalWash(shifts: List<Shift>): Double {
        var totalWash = 0.0
        shifts.indices.forEach { i ->
            totalWash += shifts[i].wash
        }
        return totalWash
    }

    fun calcTotalEarnings(shifts: List<Shift>): Double {
        var totalEarnings = 0.0
        shifts.indices.forEach { i ->
            totalEarnings += shifts[i].earnings
        }
        return totalEarnings
    }

    fun calcTotalProfit(shifts: List<Shift>): Double {
        var totalProfit = 0.0
        shifts.indices.forEach { i ->
            totalProfit += shifts[i].profit
        }
        return totalProfit
    }

    fun centsRound(n: Double): Double {
        return Math.round(n * 100) / 100.toDouble()
    }

    private fun oneRound(n: Double): Double {
        return Math.round(n * 10) / 10.toDouble()
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

    fun msToHours(ms: Long): Double {
        return ((ms / 60.0 / 60.0 / 1000.0) * 100.0).toInt() / 100.0
    }
}