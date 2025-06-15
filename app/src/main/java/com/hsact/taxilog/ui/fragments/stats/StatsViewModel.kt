package com.hsact.taxilog.ui.fragments.stats

import androidx.lifecycle.ViewModel
import com.hsact.taxilog.data.db.DBHelper
import com.hsact.taxilog.data.model.Shift
import com.hsact.taxilog.data.repository.ShiftRepositoryLegacy
import com.hsact.taxilog.data.utils.ShiftStatsUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StatsViewModel : ViewModel() {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    var shifts = mutableListOf<Shift>()
    var shiftsOrigin = mutableListOf<Shift>()
    var startDate: String? = null
    var endDate: String? = null
    val shiftsCount: String get() = shifts.size.toString()
    val avErPh: String get() = ShiftStatsUtil.calcAverageEarningsPerHour(shifts).toString()
    val avProfitPh: String get() = ShiftStatsUtil.calcAverageProfitPerHour(shifts).toString()
    val avDuration: String get() = ShiftStatsUtil.calcAverageShiftDuration(shifts).toString()
    val avMileage: String get() = ShiftStatsUtil.calcAverageMileage(shifts).toString()
    val totalDuration: String get() = ShiftStatsUtil.calcTotalShiftDuration(shifts).toString()
    val totalMileage: String get() = ShiftStatsUtil.calcTotalMileage(shifts).toString()
    val totalWash: String get() = ShiftStatsUtil.calcTotalWash(shifts).toString()
    val totalEarnings: String get() = ShiftStatsUtil.calcTotalEarnings(shifts).toString()
    val totalProfit: String get() = ShiftStatsUtil.calcTotalProfit(shifts).toString()
    val avFuel: String get() = ShiftStatsUtil.calcAverageFuelCost(shifts).toString()
    val totalFuel: String get() = ShiftStatsUtil.calcTotalFuelCost(shifts).toString()

    fun defineDates() {
        val now = LocalDateTime.now()
        val currentDate = now.toLocalDate()
        val firstDayOfMonth = now.toLocalDate().withDayOfMonth(1)
        startDate = firstDayOfMonth.format(formatter)
        endDate = currentDate.format(formatter)
    }

    fun defineShifts(db: DBHelper) {
        shiftsOrigin = ShiftRepositoryLegacy(db).getAllShifts()
        shifts = shiftsOrigin.toMutableList()
    }

    fun updateShifts(db: DBHelper) {
        shifts = ShiftRepositoryLegacy(db).getShiftsInDateRange(startDate,
            endDate).toMutableList()
    }
}