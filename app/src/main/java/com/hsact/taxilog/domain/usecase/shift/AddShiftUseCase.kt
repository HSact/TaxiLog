package com.hsact.taxilog.domain.usecase.shift

import com.hsact.taxilog.domain.repository.ShiftRepository
import com.hsact.taxilog.domain.model.Shift
import javax.inject.Inject

class AddShiftUseCase @Inject constructor(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke(shift: Shift) {
        repository.insertShift(shift)
    }
}