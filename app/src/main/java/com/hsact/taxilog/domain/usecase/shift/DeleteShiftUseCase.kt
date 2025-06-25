package com.hsact.taxilog.domain.usecase.shift

import com.hsact.taxilog.domain.repository.ShiftRepository
import com.hsact.taxilog.domain.model.Shift
import javax.inject.Inject

class DeleteShiftUseCase @Inject constructor(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke(shift: Shift) {
        repository.deleteShift(shift)
    }
}