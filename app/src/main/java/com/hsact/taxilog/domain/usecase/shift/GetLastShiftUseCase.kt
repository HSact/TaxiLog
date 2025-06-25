package com.hsact.taxilog.domain.usecase.shift

import com.hsact.taxilog.domain.model.Shift
import com.hsact.taxilog.domain.repository.ShiftRepository
import javax.inject.Inject

class GetLastShiftUseCase @Inject constructor(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke(): Shift? {
        return repository.getLastShift()
    }
}