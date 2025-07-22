package com.hsact.domain.usecase.shift

import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository

class GetLastShiftUseCase(
    private val repository: ShiftRepository,
) {
    suspend operator fun invoke(): Shift? {
        return repository.getLastShift()
    }
}