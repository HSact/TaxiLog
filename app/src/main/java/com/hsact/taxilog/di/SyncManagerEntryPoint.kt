package com.hsact.taxilog.di

import com.hsact.data.firebase.ShiftSyncManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SyncManagerEntryPoint {
    fun shiftSyncManager(): ShiftSyncManager
}