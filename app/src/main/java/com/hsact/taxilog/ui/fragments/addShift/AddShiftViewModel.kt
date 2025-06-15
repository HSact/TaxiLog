package com.hsact.taxilog.ui.fragments.addShift

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hsact.taxilog.data.db.DBHelper
import com.hsact.taxilog.data.model.Shift
import com.hsact.taxilog.data.repository.ShiftRepository
import com.hsact.taxilog.data.repository.SettingsRepositoryImpl
import com.hsact.taxilog.data.utils.ShiftStatsUtil
import com.hsact.taxilog.data.utils.ShiftStatsUtil.convertLongToTime
import com.hsact.taxilog.data.utils.ShiftStatsUtil.convertTimeToLong
import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.ui.shift.mappers.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddShiftViewModel @Inject constructor(
    private val settings: SettingsRepositoryImpl,
) : ViewModel() {
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

        var uiState = UiState().copy(timeBegin = beginTime, timeEnd = endTime)

        if (convertTimeToLong(beginTime) > convertTimeToLong(endTime)) {
            uiState = uiState.copy(date = beginDate.format(formatter))
        } else {
            uiState = uiState.copy(date = endDate.format(formatter))
        }
        _shiftData.value = uiState
        //----------------------NEW
//        val shiftInput = uiState.shiftInput
//        shiftInput.date = uiState.date
//        shiftInput.timeStart = uiState.timeBegin
//        shiftInput.timeEnd = uiState.timeEnd
//        _shiftData.value = uiState.copy(shiftInput = shiftInput)
    }

    fun guessFuelCost(context: Context) {
        if (!settings.isConfigured) return
        if (_shiftData.value?.mileage == 0.0) return
        if (settings.fuelPrice.isNullOrEmpty() || settings.consumption.isNullOrEmpty()) return
        var currentShift = _shiftData.value ?: return
        val fuelPrice: Double = (settings.fuelPrice ?: return).toDouble()
        val consumption = (settings.consumption ?: return).toDouble()
        if (!settings.isConfigured || fuelPrice == 0.0 || consumption == 0.0) {
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
        val uiState = _shiftData.value ?: return
        val shiftInput = uiState.shiftInput
        shiftInput.date = uiState.date
        shiftInput.timeStart = uiState.timeBegin
        shiftInput.timeEnd = uiState.timeEnd
        shiftInput.breakStart = uiState.breakBegin
        shiftInput.breakEnd = uiState.breakEnd
        shiftInput.earnings = uiState.earnings.toString()
        shiftInput.wash = uiState.wash.toString()
        shiftInput.fuelCost = uiState.fuelCost.toString()
        shiftInput.mileage = uiState.mileage.toString()
        shiftInput.taxRate = settings.taxRate ?: ""
        shiftInput.rentCost = settings.rentCost ?: ""
        shiftInput.serviceCost = settings.serviceCost ?: ""
        shiftInput.consumption = settings.consumption ?: ""
        _shiftData.value = uiState.copy(shiftInput = shiftInput)

        /*val carSnapshot = CarSnapshot(
            name = "",
            mileage = 0,
            fuelConsumption = (settings.consumption ?: "").toLongOrNull() ?: 0,
            rentCost = (settings.rentCost ?: "").toLongOrNull() ?: 0,
            serviceCost = (settings.serviceCost ?: "").toLongOrNull() ?: 0,
        )*/
        val shiftV2: ShiftV2 = shiftInput.toDomain()
        val shiftRepository = ShiftRepository(DBHelper(context, null))
        val shift =
            Shift(
                0, uiState.date, ShiftStatsUtil.msToHours(uiState.totalTime).toString(),
                uiState.earnings, uiState.wash, uiState.fuelCost,
                uiState.mileage, uiState.profit
            )
        shiftRepository.addShift(shift)
    }

    private fun hoursToMs(hours: Int): Long {
        return (hours * 60 * 60 * 1000).toLong()
    }
}