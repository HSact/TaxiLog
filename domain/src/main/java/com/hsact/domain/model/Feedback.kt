package com.hsact.domain.model

import java.time.LocalDateTime

/**
 * Model representing a user feedback or bug report.
 */
data class Feedback(
    val userId: String,
    val message: String,
    val timestamp: LocalDateTime,
    val appVersion: String,
    val deviceModel: String,
    val osVersion: Int,
)
