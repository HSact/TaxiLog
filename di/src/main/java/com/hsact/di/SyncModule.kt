package com.hsact.di

import com.hsact.data.sync.ShiftRemoteControllerImpl
import com.hsact.domain.sync.RemoteShiftController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Singleton
    @Binds
    abstract fun bindRemoteShiftController(
        impl: ShiftRemoteControllerImpl
    ): RemoteShiftController
}