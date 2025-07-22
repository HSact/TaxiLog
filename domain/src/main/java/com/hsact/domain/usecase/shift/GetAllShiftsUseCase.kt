package com.hsact.domain.usecase.shift

import com.hsact.domain.model.Shift
import com.hsact.domain.repository.ShiftRepository

class GetAllShiftsUseCase(
    private val repository: ShiftRepository,
) {
    suspend operator fun invoke(): List<Shift> = repository.getAllShifts()
}