package com.hsact.data.worker

import com.hsact.data.firebase.FirebaseShiftDataSource
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@EntryPoint
@InstallIn(SingletonComponent::class)
interface FirebaseShiftEntryPoint {
    fun firebaseShiftDataSource(): FirebaseShiftDataSource
}