package com.hsact.taxilog.data.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val carId: Int,
    @Embedded val carSnapshot: CarSnapshotEntity,
    @Embedded val time: ShiftTimeEntity,
    @Embedded val money: ShiftMoneyEntity,
    val mileage: Long,
    val note: String?
)