package com.hsact.domain.model

import java.time.LocalDateTime

data class ShiftMeta(
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val lastModifiedBy: String,
    val isSynced: Boolean = false,
)