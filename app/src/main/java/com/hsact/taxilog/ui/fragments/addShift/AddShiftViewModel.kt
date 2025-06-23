package com.hsact.taxilog.ui.fragments.addShift

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hsact.taxilog.domain.utils.DeprecatedDateFormatter
import com.hsact.taxilog.domain.model.Shift
import com.hsact.taxilog.domain.model.UserSettings
import com.hsact.taxilog.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.taxilog.domain.usecase.shift.AddShiftUseCase
import com.hsact.taxilog.ui.shift.ShiftInputModel
import com.hsact.taxilog.ui.shift.mappers.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class AddShiftViewModel @Inject constructor(
    getAllSettingsUseCase: GetAllSettingsUseCase,
    private val addShiftUseCase: AddShiftUseCase
) : ViewModel() {
    private val _shiftData = MutableLiveData<UiState>()
    val shiftData: LiveData<UiState> get() = _shiftData

    private val settings: UserSettings = getAllSettingsUseCase.invoke()

    init {
        loadGuess()
    }

    private fun loadGuess() {
        val formatter = DeprecatedDateFormatter
        val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

        val now = LocalDateTime.now()
        val endDate = now.toLocalDate()
        val endTime = now.toLocalTime().format(timeFormatter)

        val beginTime = convertLongToTime(convertTimeToLong(endTime) - hoursToMs(10))
        val beginDate = endDate.minusDays(1)

        var uiState = UiState(timeBegin = beginTime, timeEnd = endTime)

        uiState = if (convertTimeToLong(beginTime) > convertTimeToLong(endTime)) {
            uiState.copy(date = beginDate.format(formatter))
        } else {
            uiState.copy(date = endDate.format(formatter))
        }
        _shiftData.value = uiState
    }

    fun guessFuelCost() {
        if (!settings.isConfigured) return
        if (_shiftData.value?.mileage == 0.0) return
        if (settings.fuelPrice.isNullOrEmpty() || settings.consumption.isNullOrEmpty()) return
        var currentShift = _shiftData.value ?: return
        val fuelPrice: Double = (settings.fuelPrice).toDouble()
        val consumption = (settings.consumption).toDouble()
        if (fuelPrice == 0.0 || consumption == 0.0) {
            return
        }
        if (currentShift.mileage == 0.0) {
            return
        }
        currentShift = currentShift.copy(
            fuelCost = centsRound(
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

    suspend fun submit() {
        val uiState = _shiftData.value ?: return
        val shiftInput = buildShiftInputModel(uiState)

        val shift: Shift = shiftInput.toDomain()
        addShiftUseCase(shift)
    }

    private fun buildShiftInputModel(
        uiState: UiState,
    ): ShiftInputModel {
        val shiftInput = ShiftInputModel()
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
        return shiftInput
    }

    private fun hoursToMs(hours: Int): Long {
        return (hours * 60 * 60 * 1000).toLong()
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("H:mm")
        return format.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    private fun convertTimeToLong(date: String): Long {
        val df = SimpleDateFormat("H:mm")
        return df.parse(date)!!.time
    }

    private fun centsRound(n: Double): Double {
        return (n * 100).roundToInt() / 100.toDouble()
    }
}