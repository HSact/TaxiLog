package com.hsact.data.firebase

import com.hsact.data.firebase.model.FirebaseCarSnapshot
import com.hsact.data.firebase.model.FirebaseDateTimePeriod
import com.hsact.data.firebase.model.FirebaseFinanceInput
import com.hsact.data.firebase.model.FirebaseShift
import com.hsact.data.firebase.model.FirebaseShiftMeta
import com.hsact.domain.model.Shift
import com.hsact.domain.model.ShiftFinanceInput
import com.hsact.domain.model.ShiftMeta
import com.hsact.domain.model.car.CarSnapshot
import com.hsact.domain.model.time.DateTimePeriod
import com.hsact.domain.model.time.ShiftTime

fun Shift.toFirebase(): FirebaseShift = FirebaseShift(
    id = id,
    remoteId = remoteId,
    carId = carId,
    meta = meta.toFirebase(),
    carSnapshot = carSnapshot.toFirebase(),
    period = time.period.toFirebase(),
    rest = time.rest?.toFirebase(),
    financeInput = financeInput.toFirebase(),
    note = note
)

fun FirebaseShift.toDomainOrNull(): Shift? {
    if (meta == null || carSnapshot == null || period == null || financeInput == null) return null
    return Shift(
        id = id ?: 0,
        remoteId = remoteId,
        carId = carId ?: 0,
        meta = meta.toDomain(),
        carSnapshot = carSnapshot.toDomain(),
        time = ShiftTime(
            period = period.toDomain(),
            rest = rest?.toDomain()
        ),
        financeInput = financeInput.toDomain(),
        note = note
    )
}

fun ShiftMeta.toFirebase(): FirebaseShiftMeta = FirebaseShiftMeta(
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
    lastModifiedBy = lastModifiedBy,
    isSynced = isSynced
)

fun FirebaseShiftMeta.toDomain(): ShiftMeta = ShiftMeta(
    createdAt = createdAt?.let { java.time.LocalDateTime.parse(it) } ?: java.time.LocalDateTime.now(),
    updatedAt = updatedAt?.let { java.time.LocalDateTime.parse(it) } ?: java.time.LocalDateTime.now(),
    lastModifiedBy = lastModifiedBy ?: "",
    isSynced = isSynced ?: false
)

fun CarSnapshot.toFirebase(): FirebaseCarSnapshot = FirebaseCarSnapshot(
    name = name,
    mileage = mileage,
    fuelConsumption = fuelConsumption,
    rentCost = rentCost,
    serviceCost = serviceCost
)

fun FirebaseCarSnapshot.toDomain(): CarSnapshot = CarSnapshot(
    name = name ?: "",
    mileage = mileage ?: 0,
    fuelConsumption = fuelConsumption ?: 0,
    rentCost = rentCost ?: 0,
    serviceCost = serviceCost ?: 0
)

fun DateTimePeriod.toFirebase(): FirebaseDateTimePeriod = FirebaseDateTimePeriod(
    start = start.toString(),
    end = end.toString()
)

fun FirebaseDateTimePeriod.toDomain(): DateTimePeriod = DateTimePeriod(
    start = start?.let { java.time.LocalDateTime.parse(it) } ?: java.time.LocalDateTime.now(),
    end = end?.let { java.time.LocalDateTime.parse(it) } ?: java.time.LocalDateTime.now()
)

fun ShiftFinanceInput.toFirebase(): FirebaseFinanceInput = FirebaseFinanceInput(
    earnings = earnings,
    tips = tips,
    taxRate = taxRate,
    wash = wash,
    fuelCost = fuelCost
)

fun FirebaseFinanceInput.toDomain(): ShiftFinanceInput = ShiftFinanceInput(
    earnings = earnings ?: 0,
    tips = tips ?: 0,
    taxRate = taxRate ?: 0,
    wash = wash ?: 0,
    fuelCost = fuelCost ?: 0
)