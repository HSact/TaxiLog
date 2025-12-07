package com.hsact.domain.usecase.shift

import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository
import kotlinx.coroutines.flow.Flow

class GetLastShiftUseCase(
    private val repository: ShiftRepository,
) {
    operator fun invoke(): Flow<Shift?> {
        return repository.getLastShift()
    }
}