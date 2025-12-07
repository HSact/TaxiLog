package com.hsact.domain.usecase.shift

import com.hsact.domain.repository.ShiftRepository

class GetShiftByIdUseCase(
    private val repository: ShiftRepository,
) {
    operator fun invoke(id: Int) = repository.getShift(id)
}