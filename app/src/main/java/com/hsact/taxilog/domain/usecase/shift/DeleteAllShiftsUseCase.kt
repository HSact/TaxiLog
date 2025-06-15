package com.hsact.taxilog.domain.usecase.shift

import com.hsact.taxilog.data.repository.ShiftRepository
import javax.inject.Inject

class DeleteAllShiftsUseCase @Inject constructor(
    private val repository: ShiftRepository
) {
    suspend operator fun invoke() {
        repository.deleteAll()
        repository.resetPrimaryKey()
    }
}