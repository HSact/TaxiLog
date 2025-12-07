package com.hsact.domain.usecase.shift

import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository
import kotlinx.coroutines.flow.Flow

class GetAllShiftsUseCase(
    private val repository: ShiftRepository,
) {
    operator fun invoke(): Flow<List<Shift>> = repository.getAllShifts()
}