package com.hsact.domain.usecase.shift

import com.hsact.domain.repository.ShiftRepository

class GetShiftByIdUseCase(
    private val repository: ShiftRepository,
) {
    suspend operator fun invoke(id: Int) = repository.getShift(id)
}