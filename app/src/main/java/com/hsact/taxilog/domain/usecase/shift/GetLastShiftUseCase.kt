package com.hsact.taxilog.domain.usecase.shift

import com.hsact.taxilog.domain.model.ShiftV2
import com.hsact.taxilog.domain.repository.ShiftRepository
import javax.inject.Inject

class GetLastShiftUseCase @Inject constructor(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke(): ShiftV2? {
        return repository.getLastShift()
    }
}