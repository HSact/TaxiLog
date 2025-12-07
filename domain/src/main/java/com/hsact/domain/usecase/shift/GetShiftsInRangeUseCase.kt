package com.hsact.domain.usecase.shift

import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime


class GetShiftsInRangeUseCase(
    private val repository: ShiftRepository,
) {
    operator fun invoke(start: LocalDateTime, end: LocalDateTime): Flow<List<Shift>> =
        repository.getShiftsInRange(start, end)
}