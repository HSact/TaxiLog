package com.hsact.data.sync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.hsact.data.firebase.FirebaseShiftDataSource
import com.hsact.data.sync.ShiftSyncManager
import com.hsact.domain.usecase.shift.GetShiftByIdUseCase
import com.hsact.domain.usecase.shift.UpdateShiftUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

abstract class BaseFirebaseWorker(
    context: Context,
    workerParams: WorkerParameters
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
    interface GetShiftByIdUseCaseEntryPoint {
        fun getShiftByIdUseCase(): GetShiftByIdUseCase
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UpdateShiftUseCaseEntryPoint {
        fun updateShiftUseCase(): UpdateShiftUseCase
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

    protected val getShiftByIdUseCase: GetShiftByIdUseCase by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            GetShiftByIdUseCaseEntryPoint::class.java
        ).getShiftByIdUseCase()
    }

    protected val updateShiftUseCase: UpdateShiftUseCase by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            UpdateShiftUseCaseEntryPoint::class.java
        ).updateShiftUseCase()
    }
}