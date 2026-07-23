package com.hsact.data.mappers

import com.google.firebase.auth.FirebaseUser
import com.hsact.domain.model.User

/**
 * Converts a FirebaseUser to a domain User model.
 */
fun FirebaseUser.toDomain(): User {
    return User(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString()
    )
}