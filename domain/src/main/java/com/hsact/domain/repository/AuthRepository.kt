package com.hsact.domain.repository

import com.hsact.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface for authentication operations.
 */
interface AuthRepository {
    /**
     * Returns a flow of the currently authenticated user.
     * Emits null if the user is not authenticated.
     */
    fun authState(): Flow<User?>

    /**
     * Returns the currently authenticated user, if any.
     */
    fun getCurrentUser(): User?

    /**
     * Authenticates with Google using the provided ID token.
     */
    suspend fun signInWithGoogle(idToken: String): Result<User>

    /**
     * Signs out the current user.
     */
    suspend fun signOut()
}
