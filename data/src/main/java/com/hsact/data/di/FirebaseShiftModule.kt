package com.hsact.data.di

import com.hsact.data.firebase.datasource.FirebaseShiftDataSource
import com.hsact.data.firebase.datasource.FirebaseShiftDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseShiftModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseShiftDataSource(
        impl: FirebaseShiftDataSourceImpl
    ): FirebaseShiftDataSource
}