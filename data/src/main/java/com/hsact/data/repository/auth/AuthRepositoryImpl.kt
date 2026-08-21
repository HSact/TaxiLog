package com.hsact.data.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hsact.data.mappers.toDomain
import com.hsact.domain.model.User
import com.hsact.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl
    @Inject
    constructor(
        private val firebaseAuth: FirebaseAuth,
    ) : AuthRepository {
        override fun authState(): Flow<User?> =
            callbackFlow {
                val listener = FirebaseAuth.AuthStateListener { auth ->
                    trySend(auth.currentUser?.toDomain())
                }
                firebaseAuth.addAuthStateListener(listener)
                awaitClose { firebaseAuth.removeAuthStateListener(listener) }
            }

        override fun getCurrentUser(): User? = firebaseAuth.currentUser?.toDomain()

        override suspend fun signInWithGoogle(idToken: String): Result<User> =
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()
                val user = result.user?.toDomain()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Failed to get user after sign in"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun signOut() {
            firebaseAuth.signOut()
        }
    }
