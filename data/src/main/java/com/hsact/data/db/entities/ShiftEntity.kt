package com.hsact.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val remoteId: String?,
    val carId: Int,
    @Embedded(prefix = "meta_") val meta: ShiftMetaEntity,
    @Embedded(prefix = "car_") val carSnapshot: CarSnapshotEntity,
    @Embedded(prefix = "period_") val period: DateTimePeriodEntity,
    @Embedded(prefix = "rest_") val rest: DateTimePeriodEntity? = null,
    @Embedded val financeInput: ShiftFinanceInputEntity,
    val note: String?
)