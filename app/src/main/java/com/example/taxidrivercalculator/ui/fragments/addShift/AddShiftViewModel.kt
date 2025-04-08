package com.example.taxidrivercalculator.ui.fragments.addShift

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taxidrivercalculator.helpers.DBHelper
import com.example.taxidrivercalculator.helpers.SettingsHelper
import com.example.taxidrivercalculator.helpers.ShiftHelper
import com.example.taxidrivercalculator.helpers.ShiftHelper.convertLongToTime
import com.example.taxidrivercalculator.helpers.ShiftHelper.convertTimeToLong
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class AddShiftData
    (var date: String ="",
     var timeBegin: String ="",
     var timeEnd: String ="",
     var breakBegin: String ="",
     var breakEnd: String ="",
     var onlineTime: Long = 0,
     var breakTime: Long = 0,
     var totalTime: Long = 0,
     var earnings: Double = 0.0,
     var wash: Double = 0.0,
     var fuelCost: Double = 0.0,
     var mileage: Double = 0.0,
     var profit: Double = 0.0)

class AddShiftViewModel: ViewModel() {
    private val _shiftData = MutableLiveData<AddShiftData>()
    val shiftData: LiveData<AddShiftData> get() = _shiftData
    init { loadGuess() }

    private fun loadGuess() {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

        val now = LocalDateTime.now()
        val endDate = now.toLocalDate()
        val endTime = now.toLocalTime().format(timeFormatter)

        val beginTime = convertLongToTime(convertTimeToLong(endTime) - hoursToMs(10))
        val beginDate = endDate.minusDays(1)

        var currentShift = AddShiftData().copy(timeBegin = beginTime, timeEnd = endTime)

        if (convertTimeToLong(beginTime) > convertTimeToLong(endTime)) {
            currentShift = currentShift.copy(date = beginDate.format(formatter))
        }
        else
        {
            currentShift = currentShift.copy(date = endDate.format(formatter))
        }
        _shiftData.value = currentShift
    }

    fun guessFuelCost (context: Context)
    {
        val settings = SettingsHelper.getInstance(context)
        var currentShift = _shiftData.value ?: return
        val fuelPrice: Double = (settings.fuelPrice ?: 0.0.toString()).toDouble()
        val consumption = (settings.consumption ?: 0.0.toString()).toDouble()
        if (!settings.seted_up || fuelPrice == 0.0 || consumption == 0.0)
        {
            return
        }
        if (currentShift.mileage == 0.0)
        {
            return
        }
        currentShift = currentShift.copy(fuelCost = ShiftHelper.centsRound(
            fuelPrice * currentShift.mileage * consumption / 100))
        _shiftData.value = currentShift
    }

    fun updateShift(shift: AddShiftData) {
        _shiftData.value = shift
    }

    fun calculateShift ()
    {
        var currentShift = _shiftData.value ?: return
        currentShift = currentShift.copy(onlineTime = convertTimeToLong(currentShift.timeEnd) - convertTimeToLong(currentShift.timeBegin))
        if (currentShift.onlineTime <0)
        {
            currentShift.onlineTime += hoursToMs(24)
        }
        if (currentShift.breakBegin.isNotEmpty() && currentShift.breakEnd.isNotEmpty())
        {
            currentShift.breakTime = convertTimeToLong(currentShift.breakEnd)-convertTimeToLong(currentShift.breakBegin)
            if (currentShift.breakTime < 0)
            {
                currentShift.breakTime += hoursToMs(24)
            }
            currentShift.totalTime = currentShift.onlineTime - currentShift.breakTime
        }
        else
        {
            currentShift.totalTime = currentShift.onlineTime
        }
        currentShift.profit = currentShift.earnings - currentShift.wash - currentShift.fuelCost
        _shiftData.value = currentShift
    }

    fun submit(context: Context)
    {
        val currentShift = _shiftData.value ?: return
        val db = DBHelper(context, null)
        db.addShift(
            currentShift.date,
            ShiftHelper.msToHours(currentShift.totalTime),
            currentShift.earnings,
            currentShift.wash,
            currentShift.fuelCost,
            currentShift.mileage,
            currentShift.profit
        )
    }

    private fun hoursToMs (hours: Int): Long
    {
        return (hours*60*60*1000).toLong()
    }
}