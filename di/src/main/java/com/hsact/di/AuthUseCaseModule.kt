package com.hsact.di

import com.hsact.domain.repository.AuthRepository
import com.hsact.domain.usecase.auth.GetAuthStateUseCase
import com.hsact.domain.usecase.auth.GetCurrentUserUseCase
import com.hsact.domain.usecase.auth.SignInWithGoogleUseCase
import com.hsact.domain.usecase.auth.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthUseCaseModule {

    @Provides
    fun provideGetAuthStateUseCase(
        repository: AuthRepository
    ): GetAuthStateUseCase = GetAuthStateUseCase(repository)

    @Provides
    fun provideGetCurrentUserUseCase(
        repository: AuthRepository
    ): GetCurrentUserUseCase = GetCurrentUserUseCase(repository)

    @Provides
    fun provideSignInWithGoogleUseCase(
        repository: AuthRepository
    ): SignInWithGoogleUseCase = SignInWithGoogleUseCase(repository)

    @Provides
    fun provideSignOutUseCase(
        repository: AuthRepository
    ): SignOutUseCase = SignOutUseCase(repository)
}