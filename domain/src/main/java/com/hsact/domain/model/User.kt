package com.hsact.domain.model

/**
 * Domain model representing an authenticated user.
 */
data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)