package com.hsact.taxilog.ui.fragments.shiftForm

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsact.domain.model.Shift
import com.hsact.domain.model.ShiftMeta
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetDeviceIdUseCase
import com.hsact.domain.usecase.settings.GetSettingsFlowUseCase
import com.hsact.domain.usecase.shift.AddShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftByIdUseCase
import com.hsact.domain.usecase.shift.GetShiftSequenceNumberUseCase
import com.hsact.domain.utils.DeprecatedDateFormatter
import com.hsact.domain.utils.centsToDollars
import com.hsact.domain.utils.toShortDate
import com.hsact.domain.utils.toShortTime
import com.hsact.taxilog.ui.shift.ShiftInputModel
import com.hsact.taxilog.ui.shift.mappers.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ShiftFormViewModel
    @Inject
    constructor(
        getSettingsFlowUseCase: GetSettingsFlowUseCase,
        private val getDeviceIdUseCase: GetDeviceIdUseCase,
        private val addShiftUseCase: AddShiftUseCase,
        private val getShiftByIdUseCase: GetShiftByIdUseCase,
        private val getShiftSequenceNumberUseCase: GetShiftSequenceNumberUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(UiState())
        val uiState: StateFlow<UiState> = _uiState.asStateFlow()

        private val _sequenceNumber = MutableStateFlow<Int?>(null)

        /**
         * The reactive sequence number of the shift for display.
         */
        val sequenceNumber: StateFlow<Int?> = _sequenceNumber

        /**
         * Reactive user settings used for pre-filling the shift form and guessing costs.
         */
        val settings: StateFlow<UserSettings> =
            getSettingsFlowUseCase()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings.default)

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

            uiState =
                if (convertTimeToLong(beginTime) > convertTimeToLong(endTime)) {
                    uiState.copy(date = beginDate.format(formatter))
                } else {
                    uiState.copy(date = endDate.format(formatter))
                }
            _uiState.value = uiState
        }

        /**
         * Attempts to guess the fuel cost based on mileage, fuel price, and consumption settings.
         */
        fun guessFuelCost() {
            val currentSettings = settings.value
            val currentShift = _uiState.value

            val canGuess =
                currentSettings.isConfigured &&
                    currentShift.mileage != 0.0 &&
                    !currentSettings.fuelPrice.isNullOrEmpty() &&
                    !currentSettings.consumption.isNullOrEmpty()

            if (canGuess) {
                val fuelPrice: Double = (currentSettings.fuelPrice!!).toDouble()
                val consumption = (currentSettings.consumption!!).toDouble()

                if (fuelPrice != 0.0 && consumption != 0.0) {
                    _uiState.value =
                        currentShift.copy(
                            fuelCost =
                                centsRound(
                                    fuelPrice * currentShift.mileage * consumption / 100,
                                ),
                        )
                }
            }
        }

        /**
         * Updates the current UI state with the provided [UiState].
         */
        fun updateShift(shift: UiState) {
            _uiState.value = shift
        }

        /**
         * Calculates the shift profit and total time based on input values.
         */
        fun calculateShift(
            earnings: Double,
            tips: Double,
            wash: Double,
            fuelCost: Double,
            mileage: Double,
            note: String,
        ) {
            var currentShift = _uiState.value
            currentShift =
                currentShift.copy(
                    onlineTime = convertTimeToLong(currentShift.timeEnd) - convertTimeToLong(currentShift.timeBegin),
                    mileage = mileage,
                    earnings = earnings,
                    tips = tips,
                    wash = wash,
                    fuelCost = fuelCost,
                    note = note,
                )
            if (currentShift.onlineTime < 0) {
                currentShift = currentShift.copy(onlineTime = currentShift.onlineTime + hoursToMs(24))
            }
            if (currentShift.breakBegin.isNotEmpty() && currentShift.breakEnd.isNotEmpty()) {
                currentShift =
                    currentShift.copy(
                        breakTime =
                            convertTimeToLong(currentShift.breakEnd) - convertTimeToLong(currentShift.breakBegin),
                    )
                if (currentShift.breakTime < 0) {
                    currentShift = currentShift.copy(breakTime = currentShift.breakTime + hoursToMs(24))
                }
                currentShift =
                    currentShift.copy(totalTime = currentShift.onlineTime - currentShift.breakTime)
            } else {
                currentShift = currentShift.copy(totalTime = currentShift.onlineTime)
            }
            currentShift =
                currentShift.copy(profit = ((earnings + tips - wash - fuelCost) * 100).roundToInt() / 100.0)
            _uiState.value = currentShift
        }

        fun setMileage(mileage: Double) {
            _uiState.value = _uiState.value.copy(mileage = mileage)
            guessFuelCost()
        }

        fun setDate(date: String) {
            _uiState.value = _uiState.value.copy(date = date)
        }

        fun setTimeBegin(time: String) {
            _uiState.value = _uiState.value.copy(timeBegin = time)
        }

        fun setTimeEnd(time: String) {
            _uiState.value = _uiState.value.copy(timeEnd = time)
        }

        fun setBreakBegin(time: String) {
            _uiState.value = _uiState.value.copy(breakBegin = time)
        }

        fun setBreakEnd(time: String) {
            _uiState.value = _uiState.value.copy(breakEnd = time)
        }

        /**
         * Submits the shift data (insert or update) and pushes to remote if configured.
         */
        fun submit() {
            val uiState = _uiState.value
            val shiftInput = buildShiftInputModel(uiState)
            val deviceId = getDeviceIdUseCase.invoke()
            val remoteId =
                if (uiState.editShift != null && uiState.editShift.remoteId != null) {
                    uiState.editShift.remoteId
                } else {
                    null
                }

            val createdAt =
                if (uiState.editShift != null) {
                    uiState.editShift.meta.createdAt
                } else {
                    LocalDateTime.now()
                }
            val shiftMeta =
                ShiftMeta(
                    createdAt = createdAt,
                    updatedAt = LocalDateTime.now(),
                    lastModifiedBy = deviceId,
                )
            val shift: Shift = shiftInput.toDomain(shiftMeta)
            viewModelScope.launch {
                addShiftUseCase(shift.copy(id = uiState.id, remoteId = remoteId))
            }
        }

        private fun buildShiftInputModel(uiState: UiState): ShiftInputModel {
            val currentSettings = settings.value
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
                taxRate = currentSettings.taxRate ?: "",
                rentCost = currentSettings.rentCost ?: "",
                serviceCost = currentSettings.serviceCost ?: "",
                consumption = currentSettings.consumption ?: "",
                note = uiState.note.ifEmpty { null },
            )
        }

        private fun hoursToMs(hours: Int): Long = (hours * 60 * 60 * 1000).toLong()

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

        private fun centsRound(n: Double): Double = (n * 100).roundToInt() / 100.toDouble()

        /**
         * Loads the shift data and its sequence number reactively.
         * @param id The technical ID of the shift.
         */
        fun loadShift(id: Int) {
            viewModelScope.launch {
                getShiftSequenceNumberUseCase(id).collect { number ->
                    _sequenceNumber.value = number
                }
            }
            viewModelScope.launch {
                val shift = getShiftByIdUseCase(id).first() ?: return@launch
                _uiState.value =
                    UiState(
                        id = shift.id,
                        editShift = shift,
                        date =
                            shift.time.period.start
                                .toShortDate(),
                        timeBegin =
                            shift.time.period.start
                                .toShortTime(),
                        timeEnd =
                            shift.time.period.end
                                .toShortTime(),
                        breakBegin =
                            if (shift.time.rest != null) {
                                shift.time.rest!!
                                    .start
                                    .toShortTime()
                            } else {
                                ""
                            },
                        breakEnd =
                            if (shift.time.rest != null) {
                                shift.time.rest!!
                                    .end
                                    .toShortTime()
                            } else {
                                ""
                            },
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
