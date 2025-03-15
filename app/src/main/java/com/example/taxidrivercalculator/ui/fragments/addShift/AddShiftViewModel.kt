package com.example.taxidrivercalculator.ui.fragments.addShift

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taxidrivercalculator.helpers.ShiftHelper.convertLongToTime
import com.example.taxidrivercalculator.helpers.ShiftHelper.convertTimeToLong
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddShiftViewModel: ViewModel() {

    object CalcShift {
        var date: String =""
        var timeBegin: String =""
        var timeEnd: String =""
        var breakBegin: String =""
        var breakEnd: String =""
        var onlineTime: Long = 0
        var breakTime: Long = 0
        var totalTime: Long = 0
        var earnings: Double = 0.0
        var wash: Double = 0.0
        var fuelCost: Double = 0.0
        var mileage: Double = 0.0
        var profit: Double = 0.0
    }

    private val _shiftData = MutableLiveData<Map<String, Any>>()
    val shiftData: LiveData<Map<String, Any>> get() = _shiftData



    private fun loadGuess() {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

        val now = LocalDateTime.now()
        val endDate = now.toLocalDate()
        val endTime = now.toLocalTime().format(timeFormatter)

        val beginTime = convertLongToTime(convertTimeToLong(endTime) - hoursToMs(10))
        val beginDate = endDate.minusDays(1)

        //_shiftData.timeEnd = endTime
        _shiftData.value = (_shiftData.value ?: emptyMap()) + ("timeBegin" to beginTime)
        _shiftData.value = (_shiftData.value ?: emptyMap()) + ("timeEnd" to endTime)
       // _shiftData.timeBegin = beginTime

        if (convertTimeToLong(beginTime) > convertTimeToLong(endTime)) {
            //_shiftData.date = beginDate.format(formatter)
            _shiftData.value = (_shiftData.value ?: emptyMap()) + ("date" to beginDate.format(formatter))
        }
        else
        {
            //_shiftData.date = endDate.format(formatter)
            _shiftData.value = (_shiftData.value ?: emptyMap()) + ("date" to endDate.format(formatter))
        }
    }

    private fun hoursToMs (hours: Int): Long
    {
        return (hours*60*60*1000).toLong()
    }

}