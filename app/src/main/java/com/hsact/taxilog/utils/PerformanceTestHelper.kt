package com.hsact.taxilog.utils

import com.hsact.domain.model.ShiftMeta
import com.hsact.domain.usecase.settings.GetDeviceIdUseCase
import com.hsact.domain.usecase.shift.AddShiftUseCase
import com.hsact.taxilog.ui.shift.ShiftInputModel
import com.hsact.taxilog.ui.shift.mappers.toDomain
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class to generate large amounts of shift data for performance testing.
 */
@Singleton
class PerformanceTestHelper
    @Inject
    constructor(
        private val addShiftUseCase: AddShiftUseCase,
        private val getDeviceIdUseCase: GetDeviceIdUseCase,
    ) {
        /**
         * Populates the database with [count] shifts following a 6/1 schedule.
         * Shifts are generated going backwards from the current date.
         */
        suspend fun populateShifts(count: Int = 1000) {
            val deviceId = getDeviceIdUseCase()
            val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            var currentDate = LocalDate.now()
            var shiftsAdded = 0
            var dayInCycle = 1 // 1-6: work days, 7: day off

            while (shiftsAdded < count) {
                if (dayInCycle <= 6) {
                    val dateStr = currentDate.format(dateFormatter)
                    val shiftInput =
                        ShiftInputModel(
                            date = dateStr,
                            timeStart = "08:00",
                            timeEnd = "20:00",
                            earnings = "5000",
                            tips = "500",
                            wash = "300",
                            fuelCost = "1500",
                            mileage = "200",
                            taxRate = "6.0",
                            rentCost = "1500",
                            serviceCost = "5",
                            consumption = "10.0",
                            note = "Performance Test Shift #$shiftsAdded",
                        )

                    val shiftMeta =
                        ShiftMeta(
                            createdAt = LocalDateTime.of(currentDate, LocalDateTime.now().toLocalTime()),
                            updatedAt = LocalDateTime.now(),
                            lastModifiedBy = deviceId,
                            isSynced = false,
                        )

                    val shift = shiftInput.toDomain(shiftMeta)
                    addShiftUseCase(shift)
                    shiftsAdded++
                }

                currentDate = currentDate.minusDays(1)
                dayInCycle = if (dayInCycle == 7) 1 else dayInCycle + 1
            }
        }
    }
