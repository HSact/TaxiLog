package com.hsact.taxilog

import com.hsact.domain.model.ShiftMeta
import com.hsact.taxilog.ui.shift.ShiftInputModel
import com.hsact.taxilog.ui.shift.mappers.toDomain
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ShiftInputToDomainTest {

    @Test
    fun `toDomain handles commas in numeric fields`() {
        val input = ShiftInputModel(
            date = "01.01.2024",
            timeStart = "08:00",
            timeEnd = "18:00",
            mileage = "123,45",
            consumption = "10,5",
            rentCost = "1500,50",
            serviceCost = "5,25",
            earnings = "5000,75",
            tips = "100,25",
            wash = "20,50",
            fuelCost = "300,40",
            taxRate = "6,5"
        )

        val meta = ShiftMeta(
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            lastModifiedBy = "test"
        )

        val domain = input.toDomain(meta)

        assertEquals(123450L, domain.carSnapshot.mileage) // 123.45 * 1000
        assertEquals(10500L, domain.carSnapshot.fuelConsumption) // 10.5 * 1000
        assertEquals(150050L, domain.carSnapshot.rentCost) // 1500.50 * 100
        assertEquals(525L, domain.carSnapshot.serviceCost) // 5.25 * 100

        assertEquals(500075L, domain.financeInput.earnings) // 5000.75 * 100
        assertEquals(10025L, domain.financeInput.tips)
        assertEquals(2050L, domain.financeInput.wash)
        assertEquals(30040L, domain.financeInput.fuelCost)
        assertEquals(650, domain.financeInput.taxRate) // 6.5 * 100
    }
}
