package com.hsact.domain.usecase.shift

import com.hsact.domain.repository.ShiftRepository

class SyncShiftsUseCase(
    private val repository: ShiftRepository,
) {
    suspend operator fun invoke() {
        repository.sync()
    }
}