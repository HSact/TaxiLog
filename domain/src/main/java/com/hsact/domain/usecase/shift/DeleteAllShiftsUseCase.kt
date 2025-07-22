package com.hsact.domain.usecase.shift

import com.hsact.domain.repository.ShiftRepository

class DeleteAllShiftsUseCase(
    private val repository: ShiftRepository,
) {
    suspend operator fun invoke() {
        repository.deleteAll()
        repository.resetPrimaryKey()
    }
}