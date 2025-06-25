package com.hsact.taxilog.di

import com.hsact.taxilog.domain.repository.ShiftRepository
import com.hsact.taxilog.data.repository.ShiftRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ShiftRepositoryModule {

    @Binds
    @Singleton
    @Suppress("unused")
    abstract fun bindShiftRepository(
        impl: ShiftRepositoryImpl,
    ): ShiftRepository
}