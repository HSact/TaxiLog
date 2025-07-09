package com.hsact.taxilog.ui.fragments.shiftForm

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.Shift
import com.hsact.domain.model.ShiftMeta
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.domain.usecase.settings.GetDeviceIdUseCase
import com.hsact.domain.usecase.shift.AddShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftByIdUseCase
import com.hsact.domain.utils.DeprecatedDateFormatter
import com.hsact.domain.utils.centsToDollars
import com.hsact.domain.utils.toShortDate
import com.hsact.domain.utils.toShortTime
import com.hsact.taxilog.ui.shift.ShiftInputModel
import com.hsact.taxilog.ui.shift.mappers.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ShiftFormViewModel @Inject constructor(
    getAllSettingsUseCase: GetAllSettingsUseCase,
    private val getDeviceIdUseCase: GetDeviceIdUseCase,
    private val addShiftUseCase: AddShiftUseCase,
    private val getShiftByIdUseCase: GetShiftByIdUseCase,
) : ViewModel() {
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> get() = _uiState

    val settings: UserSettings = getAllSettingsUseCase.invoke()

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
        _uiState.value = uiState
    }

    fun guessFuelCost() {
        if (!settings.isConfigured) return
        if (_uiState.value?.mileage == 0.0) return
        if (settings.fuelPrice.isNullOrEmpty() || settings.consumption.isNullOrEmpty()) return
        var currentShift = _uiState.value ?: return
        val fuelPrice: Double = (settings.fuelPrice!!).toDouble()
        val consumption = (settings.consumption!!).toDouble()
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
        _uiState.value = currentShift
    }

    fun updateShift(shift: UiState) {
        _uiState.value = shift
    }

    fun calculateShift(
        earnings: Double,
        tips: Double,
        wash: Double,
        fuelCost: Double,
        mileage: Double,
        note: String,
    ) {
        var currentShift = _uiState.value ?: return
        currentShift = currentShift.copy(
            onlineTime = convertTimeToLong(currentShift.timeEnd) - convertTimeToLong(currentShift.timeBegin),
            mileage = mileage,
            earnings = earnings,
            tips = tips,
            wash = wash,
            fuelCost = fuelCost,
            note = note
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
        currentShift.profit = ((earnings + tips - wash - fuelCost) * 100).roundToInt() / 100.0
        _uiState.value = currentShift
    }

    fun submit() {
        val uiState = _uiState.value ?: return
        val shiftInput = buildShiftInputModel(uiState)
        val deviceId = getDeviceIdUseCase.invoke()
        val remoteId =
            if (uiState.editShift != null && uiState.editShift.remoteId != null) uiState.editShift.remoteId
            else UUID.randomUUID().toString()

        val createdAt =
            if (uiState.editShift != null) uiState.editShift.meta.createdAt
            else LocalDateTime.now()
        val shiftMeta = ShiftMeta(
            createdAt = createdAt,
            updatedAt = LocalDateTime.now(),
            lastModifiedBy = deviceId,
        )
        val shift: Shift = shiftInput.toDomain(shiftMeta)
        viewModelScope.launch {
            addShiftUseCase(shift.copy(id = uiState.id, remoteId = remoteId))
        }
    }

    private fun buildShiftInputModel(
        uiState: UiState,
    ): ShiftInputModel {
        return ShiftInputModel(
            date = uiState.date,
            timeStart = uiState.timeBegin,
            timeEnd = uiState.timeEnd,
            breakStart = uiState.breakBegin,
            breakEnd = uiState.breakEnd,
            earnings = uiState.earnings.toString(),
            tips = uiState.tips.toString(),
            wash = uiState.wash.toString(),
            fuelCost = uiState.fuelCost.toString(),
            mileage = uiState.mileage.toString(),
            taxRate = settings.taxRate ?: "",
            rentCost = settings.rentCost ?: "",
            serviceCost = settings.serviceCost ?: "",
            consumption = settings.consumption ?: "",
            note = uiState.note.ifEmpty { null },
        )
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

    fun loadShift(id: Int) {
        viewModelScope.launch {
            val shift = getShiftByIdUseCase(id)
            if (shift == null) {
                return@launch
            }
            _uiState.value = UiState(
                id = shift.id,
                editShift = shift,
                date = shift.time.period.start.toShortDate(),
                timeBegin = shift.time.period.start.toShortTime(),
                timeEnd = shift.time.period.end.toShortTime(),
                breakBegin = if (shift.time.rest != null) {
                    shift.time.rest!!.start.toShortTime()
                } else "",
                breakEnd = if (shift.time.rest != null) {
                    shift.time.rest!!.end.toShortTime()
                } else "",
                earnings = shift.financeInput.earnings.centsToDollars(),
                tips = shift.financeInput.tips.centsToDollars(),
                wash = shift.financeInput.wash.centsToDollars(),
                fuelCost = shift.financeInput.fuelCost.centsToDollars(),
                mileage = shift.carSnapshot.mileage.toDouble() / 1000,
                profit = shift.profit.centsToDollars(),
                note = shift.note ?: "",
            )
        }
    }
}