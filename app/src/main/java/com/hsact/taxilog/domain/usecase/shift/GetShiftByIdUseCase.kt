package com.hsact.taxilog.domain.usecase.shift

import com.hsact.taxilog.data.repository.ShiftRepository
import javax.inject.Inject

class GetShiftByIdUseCase @Inject constructor(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke(id: Int) = repository.getShift(id)
}