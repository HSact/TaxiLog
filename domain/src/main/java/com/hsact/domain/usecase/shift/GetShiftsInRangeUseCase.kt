package com.hsact.domain.usecase.shift

import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository
import java.time.LocalDateTime


class GetShiftsInRangeUseCase(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke(start: LocalDateTime, end: LocalDateTime): List<Shift> =
        repository.getShiftsInRange(start, end)
}