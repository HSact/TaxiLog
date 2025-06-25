package com.hsact.taxilog.data.mappers

import com.hsact.taxilog.data.db.entities.CarSnapshotEntity
import com.hsact.taxilog.data.db.entities.DateTimePeriodEntity
import com.hsact.taxilog.data.db.entities.ShiftEntity
import com.hsact.taxilog.data.db.entities.ShiftFinanceInputEntity
import com.hsact.taxilog.domain.model.ShiftFinanceInput
import com.hsact.taxilog.domain.model.Shift
import com.hsact.taxilog.domain.model.car.CarSnapshot
import com.hsact.taxilog.domain.model.time.DateTimePeriod
import com.hsact.taxilog.domain.model.time.ShiftTime

fun ShiftEntity.toDomain(): Shift {
    return Shift(
        id = id,
        carId = carId,
        carSnapshot = carSnapshot.toDomain(),
        time = ShiftTime(
            period = period.toDomain(),
            rest = rest?.toDomain()
        ),
        financeInput = financeInput.toDomain(),
        note = note
    )
}

fun CarSnapshotEntity.toDomain(): CarSnapshot {
    return CarSnapshot(
        name = name,
        mileage = mileage,
        fuelConsumption = fuelConsumption,
        serviceCost = serviceCost
    )
}

fun DateTimePeriodEntity.toDomain(): DateTimePeriod {
    return DateTimePeriod(
        start = this.start,
        end = this.end
    )
}

fun ShiftFinanceInputEntity.toDomain(): ShiftFinanceInput {
    return ShiftFinanceInput(
        earnings = earnings,
        wash = wash,
        fuelCost = fuelCost
    )
}

fun Shift.toEntity(): ShiftEntity = ShiftEntity(
    id = id,
    carId = carId,
    carSnapshot = carSnapshot.toEntity(),
    period = time.period.toEntity(),
    rest = time.rest?.toEntity(),
    financeInput = financeInput.toEntity(),
    note = note
)

fun CarSnapshot.toEntity(): CarSnapshotEntity = CarSnapshotEntity(
    name = name,
    mileage = mileage,
    fuelConsumption = fuelConsumption,
    rentCost = rentCost,
    serviceCost = serviceCost
)

fun DateTimePeriod.toEntity(): DateTimePeriodEntity = DateTimePeriodEntity(
    start = start,
    end = end
)

fun ShiftFinanceInput.toEntity(): ShiftFinanceInputEntity = ShiftFinanceInputEntity(
    earnings = earnings,
    wash = wash,
    fuelCost = fuelCost
)