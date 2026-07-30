package com.hsact.domain.usecase.auth

import com.hsact.domain.model.User
import com.hsact.domain.repository.AuthRepository

class SignInWithGoogleUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): Result<User> = authRepository.signInWithGoogle(idToken)
}
