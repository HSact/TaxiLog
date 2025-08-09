package com.hsact.data.di

import com.hsact.data.sync.ShiftRemoteControllerImpl
import com.hsact.data.sync.ShiftRemoteController
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
    ): ShiftRemoteController
}