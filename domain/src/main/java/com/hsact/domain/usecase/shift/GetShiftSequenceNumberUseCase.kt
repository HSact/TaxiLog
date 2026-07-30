package com.hsact.domain.usecase.shift

import com.hsact.domain.repository.ShiftRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to get the sequence number of a shift reactively.
 * The sequence number represents the order of shift creation (1-based).
 */
class GetShiftSequenceNumberUseCase(
    private val repository: ShiftRepository,
) {
    /**
     * Invokes the use case to get the sequence number for the given shift ID.
     * @param id The technical ID of the shift.
     * @return A [Flow] emitting the sequence number.
     */
    operator fun invoke(id: Int): Flow<Int> = repository.getShiftSequenceNumber(id)
}
