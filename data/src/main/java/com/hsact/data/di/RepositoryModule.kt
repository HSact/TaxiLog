package com.hsact.data.di

import com.hsact.data.repository.auth.AuthRepositoryImpl
import com.hsact.data.repository.feedback.FeedbackRepositoryImpl
import com.hsact.data.repository.shift.local.ShiftRepositoryLocal
import com.hsact.data.repository.shift.local.ShiftRepositoryLocalImpl
import com.hsact.data.repository.shift.remote.ShiftRepositoryRemote
import com.hsact.data.repository.shift.remote.ShiftRepositoryRemoteImpl
import com.hsact.domain.repository.AuthRepository
import com.hsact.domain.repository.FeedbackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLocalShiftRepository(impl: ShiftRepositoryLocalImpl): ShiftRepositoryLocal

    @Binds
    @Singleton
    abstract fun bindRemoteShiftRepository(impl: ShiftRepositoryRemoteImpl): ShiftRepositoryRemote

    @Binds
    @Singleton
    abstract fun bindFeedbackRepository(impl: FeedbackRepositoryImpl): FeedbackRepository
}
