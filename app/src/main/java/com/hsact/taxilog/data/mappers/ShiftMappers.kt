package com.hsact.taxilog.data.mappers

import com.hsact.taxilog.data.db.entities.CarSnapshotEntity
import com.hsact.taxilog.data.db.entities.DateTimePeriodEntity
import com.hsact.taxilog.data.db.entities.ShiftEntity
import com.hsact.taxilog.data.db.entities.ShiftMoneyEntity
import com.hsact.taxilog.domain.shift2.ShiftMoney
import com.hsact.taxilog.domain.shift2.ShiftV2
import com.hsact.taxilog.domain.shift2.car.CarSnapshot
import com.hsact.taxilog.domain.shift2.time.DateTimePeriod
import com.hsact.taxilog.domain.shift2.time.ShiftTime

fun ShiftEntity.toDomain(): ShiftV2 {
    return ShiftV2(
        id = id,
        carId = carId,
        carSnapshot = carSnapshot.toDomain(),
        time = ShiftTime(
            period = period.toDomain(),
            rest = rest?.toDomain()
        ),
        money = money.toDomain(),
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

fun ShiftMoneyEntity.toDomain(): ShiftMoney {
    return ShiftMoney(
        earnings = earnings,
        wash = wash,
        fuelCost = fuelCost
    )
}

fun ShiftV2.toEntity(): ShiftEntity = ShiftEntity(
    id = id,
    carId = carId,
    carSnapshot = carSnapshot.toEntity(),
    period = time.period.toEntity(),
    rest = time.rest?.toEntity(),
    money = money.toEntity(),
    note = note
)

fun CarSnapshot.toEntity(): CarSnapshotEntity = CarSnapshotEntity(
    name = name,
    mileage = mileage,
    fuelConsumption = fuelConsumption,
    serviceCost = serviceCost
)

fun DateTimePeriod.toEntity(): DateTimePeriodEntity = DateTimePeriodEntity(
    start = start,
    end = end
)

fun ShiftMoney.toEntity(): ShiftMoneyEntity = ShiftMoneyEntity(
    earnings = earnings,
    wash = wash,
    fuelCost = fuelCost
)