package com.hsact.data.mappers

import com.hsact.data.db.entities.CarSnapshotEntity
import com.hsact.data.db.entities.DateTimePeriodEntity
import com.hsact.data.db.entities.ShiftEntity
import com.hsact.data.db.entities.ShiftFinanceInputEntity
import com.hsact.data.db.entities.ShiftMetaEntity
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class ShiftMapperTest {

    @Test
    fun `ShiftEntity is correctly mapped to Shift domain model`() {
        val entity = ShiftEntity(
            id = 1,
            remoteId = "abc123",
            carId = 42,
            meta = ShiftMetaEntity(
                createdAt = LocalDateTime.of(2025, 1, 1, 10, 0),
                updatedAt = LocalDateTime.of(2025, 1, 2, 12, 0),
                lastModifiedBy = "device-001",
                isSynced = true
            ),
            carSnapshot = CarSnapshotEntity(
                name = "Toyota",
                mileage = 100000,
                fuelConsumption = 10000,
                rentCost = 3500,
                serviceCost = 200
            ),
            period = DateTimePeriodEntity(
                start = LocalDateTime.of(2025, 1, 3, 8, 0),
                end = LocalDateTime.of(2025, 1, 3, 18, 0)
            ),
            rest = DateTimePeriodEntity(
                start = LocalDateTime.of(2025, 1, 3, 12, 0),
                end = LocalDateTime.of(2025, 1, 3, 12, 30)
            ),
            financeInput = ShiftFinanceInputEntity(
                earnings = 5000,
                tips = 500,
                taxRate = 5,
                wash = 200,
                fuelCost = 1000
            ),
            note = "Some notes"
        )

        val result = entity.toDomain()

        assertEquals(entity.id, result.id)
        assertEquals(entity.remoteId, result.remoteId)
        assertEquals(entity.carId, result.carId)
        assertEquals(entity.note, result.note)
        assertEquals(entity.meta.createdAt, result.meta.createdAt)
        assertEquals(entity.carSnapshot.name, result.carSnapshot.name)
        assertEquals(entity.financeInput.earnings, result.financeInput.earnings)
        assertEquals(entity.rest?.start, result.time.rest?.start)
    }
    @Test
    fun `ShiftEntity toDomain and back toEntity results in equal ShiftEntity`() {
        val original = ShiftEntity(
            id = 1,
            remoteId = "abc123",
            carId = 42,
            meta = ShiftMetaEntity(
                createdAt = LocalDateTime.of(2025, 1, 1, 10, 0),
                updatedAt = LocalDateTime.of(2025, 1, 2, 12, 0),
                lastModifiedBy = "device-001",
                isSynced = true
            ),
            carSnapshot = CarSnapshotEntity(
                name = "Toyota",
                mileage = 100000,
                fuelConsumption = 10000,
                rentCost = 3500,
                serviceCost = 200
            ),
            period = DateTimePeriodEntity(
                start = LocalDateTime.of(2025, 1, 3, 8, 0),
                end = LocalDateTime.of(2025, 1, 3, 18, 0)
            ),
            rest = DateTimePeriodEntity(
                start = LocalDateTime.of(2025, 1, 3, 12, 0),
                end = LocalDateTime.of(2025, 1, 3, 12, 30)
            ),
            financeInput = ShiftFinanceInputEntity(
                earnings = 5000,
                tips = 500,
                taxRate = 5,
                wash = 200,
                fuelCost = 1000
            ),
            note = "Round-trip test"
        )

        val roundTripped = original.toDomain().toEntity()

        assertEquals(original, roundTripped)
    }
}