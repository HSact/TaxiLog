package com.hsact.data.db.entities

import java.time.LocalDateTime

data class ShiftMetaEntity(
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastModifiedBy: String,
    val isSynced: Boolean = false,
)