package com.hsact.data.di

import com.hsact.data.repository.shift.ShiftRepositoryImpl
import com.hsact.domain.repository.ShiftRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class ShiftRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindShiftRepository(
        impl: ShiftRepositoryImpl,
    ): ShiftRepository
}