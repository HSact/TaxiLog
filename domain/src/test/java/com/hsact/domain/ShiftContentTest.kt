package com.hsact.domain

import com.hsact.domain.model.Shift
import com.hsact.domain.model.ShiftFinanceInput
import com.hsact.domain.model.ShiftMeta
import com.hsact.domain.model.car.CarSnapshot
import com.hsact.domain.model.time.DateTimePeriod
import com.hsact.domain.model.time.ShiftTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

/**
 * Unit tests for content-based equality checking in [Shift].
 */
class ShiftContentTest {

    private fun createShift(
        id: Int = 1,
        remoteId: String? = null,
        startTime: LocalDateTime = LocalDateTime.of(2025, 5, 10, 8, 0),
        endTime: LocalDateTime = LocalDateTime.of(2025, 5, 10, 18, 0),
        earnings: Long = 5000,
        mileage: Long = 150000,
        isSynced: Boolean = false,
    ): Shift = Shift(
        id = id,
        remoteId = remoteId,
        carId = 10,
        meta = ShiftMeta(
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            lastModifiedBy = "device1",
            isSynced = isSynced,
        ),
        carSnapshot = CarSnapshot(
            name = "Toyota Prius",
            mileage = mileage,
            fuelConsumption = 8000,
            rentCost = 2000,
            serviceCost = 500,
        ),
        time = ShiftTime(
            period = DateTimePeriod(start = startTime, end = endTime),
            rest = null,
        ),
        financeInput = ShiftFinanceInput(
            earnings = earnings,
            tips = 500,
            taxRate = 6,
            wash = 300,
            fuelCost = 1200,
        ),
        note = "Test note",
    )

    @Test
    fun `isSameContent returns true for shifts with identical content regardless of id and remoteId`() {
        val localShift = createShift(id = 42, remoteId = null)
        val remoteShift = createShift(id = 0, remoteId = "DOC_123")

        assertTrue(localShift.isSameContent(remoteShift))
    }

    @Test
    fun `isSameContent returns false if earnings differ`() {
        val localShift = createShift(earnings = 5000)
        val remoteShift = createShift(earnings = 6000)

        assertFalse(localShift.isSameContent(remoteShift))
    }

    @Test
    fun `isSameContent returns false if start time differs`() {
        val localShift = createShift(startTime = LocalDateTime.of(2025, 5, 10, 8, 0))
        val remoteShift = createShift(startTime = LocalDateTime.of(2025, 5, 10, 9, 0))

        assertFalse(localShift.isSameContent(remoteShift))
    }
}
