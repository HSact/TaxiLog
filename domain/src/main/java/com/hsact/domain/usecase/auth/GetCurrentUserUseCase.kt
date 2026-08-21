package com.hsact.domain.usecase.auth

import com.hsact.domain.model.User
import com.hsact.domain.repository.AuthRepository

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): User? = authRepository.getCurrentUser()
}
