package com.hsact.taxilog.domain.usecase.shift

import com.hsact.taxilog.data.repository.ShiftRepository
import com.hsact.taxilog.domain.model.ShiftV2
import javax.inject.Inject

class GetAllShiftsUseCase @Inject constructor(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke(): List<ShiftV2> = repository.getAllShifts()
}