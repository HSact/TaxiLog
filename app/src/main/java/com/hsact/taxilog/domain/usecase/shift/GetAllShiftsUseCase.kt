package com.hsact.taxilog.domain.usecase.shift

import com.hsact.taxilog.domain.repository.ShiftRepository
import com.hsact.taxilog.domain.model.Shift
import javax.inject.Inject

class GetAllShiftsUseCase @Inject constructor(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke(): List<Shift> = repository.getAllShifts()
}