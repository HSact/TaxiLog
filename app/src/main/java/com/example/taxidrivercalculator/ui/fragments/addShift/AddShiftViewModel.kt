package com.example.taxidrivercalculator.ui.fragments.addShift

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.helpers.ShiftHelper
import com.example.taxidrivercalculator.helpers.ShiftHelper.convertLongToTime
import com.example.taxidrivercalculator.helpers.ShiftHelper.convertTimeToLong
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CalcShift
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
    init { loadGuess() }

    private val _shiftData = MutableLiveData<CalcShift>()
    val shiftData: LiveData<CalcShift> get() = _shiftData

    private fun loadGuess() {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

        val now = LocalDateTime.now()
        val endDate = now.toLocalDate()
        val endTime = now.toLocalTime().format(timeFormatter)

        val beginTime = convertLongToTime(convertTimeToLong(endTime) - hoursToMs(10))
        val beginDate = endDate.minusDays(1)

        var currentShift = CalcShift().copy(timeBegin = beginTime, timeEnd = endTime)

        if (convertTimeToLong(beginTime) > convertTimeToLong(endTime)) {
            currentShift = currentShift.copy(date = beginDate.format(formatter))
        }
        else
        {
            currentShift = currentShift.copy(date = endDate.format(formatter))
        }
        _shiftData.value = currentShift
    }

    private fun guessFuelCost (settings: SharedPreferences)
    {
        var currentShift = _shiftData.value ?: return
        val fuelPrice: Double = settings.getString("Fuel_price", "0")?.toDoubleOrNull() ?: 0.0
        val consumption: Double = settings.getString("Consumption", "0")?.toDoubleOrNull() ?: 0.0
        if (!(settings.getBoolean("Seted_up", false)) || fuelPrice == 0.0 || consumption == 0.0)
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
    private fun calculateShift ()
    {
        var currentShift = _shiftData.value ?: return
        currentShift = currentShift.copy(onlineTime = convertTimeToLong(currentShift.timeEnd) - convertTimeToLong(currentShift.timeBegin))
        //AddShiftFragment.CalcShift.onlineTime = convertTimeToLong(editEnd.text.toString()) - convertTimeToLong(editStart.text.toString())
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

    private fun hoursToMs (hours: Int): Long
    {
        return (hours*60*60*1000).toLong()
    }
}