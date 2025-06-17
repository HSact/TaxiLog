package com.hsact.taxilog.domain.usecase.shift

import com.hsact.taxilog.domain.repository.ShiftRepository
import com.hsact.taxilog.domain.model.ShiftV2
import java.time.LocalDateTime
import javax.inject.Inject

class GetShiftsInRangeUseCase @Inject constructor(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke(start: LocalDateTime, end: LocalDateTime): List<ShiftV2> =
        repository.getShiftsInRange(start, end)
}