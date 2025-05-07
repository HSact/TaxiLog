package com.example.taxidrivercalculator.data.utils

import android.annotation.SuppressLint
import com.example.taxidrivercalculator.data.model.Shift
import com.example.taxidrivercalculator.data.utils.DateUtils.getCurrentWeek
import com.example.taxidrivercalculator.data.utils.DateUtils.getDayToday
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.WeekFields
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object ShiftStatsUtil {

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
                throw IllegalArgumentException("Invalid date format in shift: ${shift.date}", e)
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
        return oneRound(totalHours)
    }

    fun calcTotalMileage(shifts: List<Shift>): Double {
        var totalMileage = 0.0
        shifts.indices.forEach { i ->
            totalMileage += shifts[i].mileage
        }
        return oneRound(totalMileage)
    }

    fun calcTotalFuelCost(shifts: List<Shift>): Double {
        var totalFuel = 0.0
        shifts.indices.forEach { i ->
            totalFuel += shifts[i].fuelCost
        }
        return centsRound(totalFuel)
    }

    fun calcTotalWash(shifts: List<Shift>): Double {
        var totalWash = 0.0
        shifts.indices.forEach { i ->
            totalWash += shifts[i].wash
        }
        return centsRound(totalWash)
    }

    fun calcTotalEarnings(shifts: List<Shift>): Double {
        var totalEarnings = 0.0
        shifts.indices.forEach { i ->
            totalEarnings += shifts[i].earnings
        }
        return centsRound(totalEarnings)
    }

    fun calcTotalProfit(shifts: List<Shift>): Double {
        var totalProfit = 0.0
        shifts.indices.forEach { i ->
            totalProfit += shifts[i].profit
        }
        return centsRound(totalProfit)
    }

    fun centsRound(n: Double): Double {
        return (n * 100).roundToInt() / 100.toDouble()
    }

    private fun oneRound(n: Double): Double {
        return (n * 10).roundToInt() / 10.toDouble()
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