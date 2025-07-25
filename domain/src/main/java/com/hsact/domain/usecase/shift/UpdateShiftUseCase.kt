package com.hsact.domain.usecase.shift

import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository


class UpdateShiftUseCase(
    private val repository: ShiftRepository,
) {
    suspend operator fun invoke(shift: Shift) {
        repository.updateShift(shift)
    }
}