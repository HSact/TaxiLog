package com.hsact.taxilog.domain.usecase.shift

import com.hsact.taxilog.domain.repository.ShiftRepository
import com.hsact.taxilog.domain.model.ShiftV2
import javax.inject.Inject

class AddShiftUseCase @Inject constructor(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke(shift: ShiftV2) {
        repository.insertShift(shift)
    }
}