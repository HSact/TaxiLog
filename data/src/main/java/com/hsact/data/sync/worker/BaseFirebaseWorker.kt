package com.hsact.data.sync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hsact.data.firebase.datasource.FirebaseShiftDataSource
import com.hsact.data.sync.ShiftSyncManager
import com.hsact.domain.repository.ShiftRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

abstract class BaseFirebaseWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ShiftSyncManagerEntryPoint {
        fun syncShifts(): ShiftSyncManager
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface FirebaseShiftEntryPoint {
        fun firebaseShiftDataSource(): FirebaseShiftDataSource
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ShiftRepositoryEntryPoint {
        fun shiftRepository(): ShiftRepository
    }

    protected val shiftRepository: ShiftRepository by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            ShiftRepositoryEntryPoint::class.java
        ).shiftRepository()
    }

    protected val shiftSyncManager: ShiftSyncManager by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            ShiftSyncManagerEntryPoint::class.java
        ).syncShifts()
    }

    protected val firebaseShiftDataSource: FirebaseShiftDataSource by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            FirebaseShiftEntryPoint::class.java
        ).firebaseShiftDataSource()
    }
}