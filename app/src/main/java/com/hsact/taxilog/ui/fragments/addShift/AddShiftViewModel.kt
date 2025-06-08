package com.hsact.taxilog.ui.fragments.addShift

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hsact.taxilog.data.db.DBHelper
import com.hsact.taxilog.data.model.Shift
import com.hsact.taxilog.data.repository.ShiftRepository
import com.hsact.taxilog.helpers.SettingsHelper
import com.hsact.taxilog.data.utils.ShiftStatsUtil
import com.hsact.taxilog.data.utils.ShiftStatsUtil.convertLongToTime
import com.hsact.taxilog.data.utils.ShiftStatsUtil.convertTimeToLong
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddShiftViewModel : ViewModel() {
    private val _shiftData = MutableLiveData<UiState>()
    val shiftData: LiveData<UiState> get() = _shiftData

    init {
        loadGuess()
    }

    private fun loadGuess() {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

        val now = LocalDateTime.now()
        val endDate = now.toLocalDate()
        val endTime = now.toLocalTime().format(timeFormatter)

        val beginTime = convertLongToTime(convertTimeToLong(endTime) - hoursToMs(10))
        val beginDate = endDate.minusDays(1)

        var currentShift = UiState().copy(timeBegin = beginTime, timeEnd = endTime)

        if (convertTimeToLong(beginTime) > convertTimeToLong(endTime)) {
            currentShift = currentShift.copy(date = beginDate.format(formatter))
        } else {
            currentShift = currentShift.copy(date = endDate.format(formatter))
        }
        _shiftData.value = currentShift
    }

    fun guessFuelCost(context: Context) {
        val settings = SettingsHelper.getInstance(context)
        if (!settings.seted_up) return
        if (_shiftData.value?.mileage == 0.0) return
        if (settings.fuelPrice.isNullOrEmpty() || settings.consumption.isNullOrEmpty()) return
        var currentShift = _shiftData.value ?: return
        val fuelPrice: Double = (settings.fuelPrice ?: return).toDouble()
        val consumption = (settings.consumption ?: return).toDouble()
        if (!settings.seted_up || fuelPrice == 0.0 || consumption == 0.0) {
            return
        }
        if (currentShift.mileage == 0.0) {
            return
        }
        currentShift = currentShift.copy(
            fuelCost = ShiftStatsUtil.centsRound(
                fuelPrice * currentShift.mileage * consumption / 100
            )
        )
        _shiftData.value = currentShift
    }

    fun updateShift(shift: UiState) {
        _shiftData.value = shift
    }

    fun calculateShift() {
        var currentShift = _shiftData.value ?: return
        currentShift = currentShift.copy(
            onlineTime = convertTimeToLong(currentShift.timeEnd) - convertTimeToLong(currentShift.timeBegin)
        )
        if (currentShift.onlineTime < 0) {
            currentShift.onlineTime += hoursToMs(24)
        }
        if (currentShift.breakBegin.isNotEmpty() && currentShift.breakEnd.isNotEmpty()) {
            currentShift.breakTime =
                convertTimeToLong(currentShift.breakEnd) - convertTimeToLong(currentShift.breakBegin)
            if (currentShift.breakTime < 0) {
                currentShift.breakTime += hoursToMs(24)
            }
            currentShift.totalTime = currentShift.onlineTime - currentShift.breakTime
        } else {
            currentShift.totalTime = currentShift.onlineTime
        }
        currentShift.profit = currentShift.earnings - currentShift.wash - currentShift.fuelCost
        _shiftData.value = currentShift
    }

    fun submit(context: Context) {
        val currentShift = _shiftData.value ?: return
        val shiftRepository = ShiftRepository(DBHelper(context, null))
        val shift =
            Shift(
                0, currentShift.date, ShiftStatsUtil.msToHours(currentShift.totalTime).toString(),
                currentShift.earnings, currentShift.wash, currentShift.fuelCost,
                currentShift.mileage, currentShift.profit
            )
        shiftRepository.addShift(shift)
    }

    private fun hoursToMs(hours: Int): Long {
        return (hours * 60 * 60 * 1000).toLong()
    }
}