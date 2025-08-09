package com.hsact.data.di

import com.hsact.data.repository.shift.local.ShiftRepositoryLocal
import com.hsact.data.repository.shift.local.ShiftRepositoryLocalImpl
import com.hsact.data.repository.shift.remote.ShiftRepositoryRemote
import com.hsact.data.repository.shift.remote.ShiftRepositoryRemoteImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

//    @Binds
//    @Singleton
//    abstract fun bindShiftRepository(
//        impl: ShiftRepositoryImpl
//    ): ShiftRepository

    @Binds
    @Singleton
    abstract fun bindLocalShiftRepository(
        impl: ShiftRepositoryLocalImpl
    ): ShiftRepositoryLocal

    @Binds
    @Singleton
    abstract fun bindRemoteShiftRepository(
        impl: ShiftRepositoryRemoteImpl
    ): ShiftRepositoryRemote
}