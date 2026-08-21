package com.hsact.domain.usecase.auth

import com.hsact.domain.model.User
import com.hsact.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetAuthStateUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<User?> = authRepository.authState()
}
