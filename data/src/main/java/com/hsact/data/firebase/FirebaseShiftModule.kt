package com.hsact.data.firebase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseShiftModule {

    @Binds
    @Singleton
    abstract fun bindFirebaseShiftDataSource(
        impl: FirebaseShiftDataSourceImpl
    ): FirebaseShiftDataSource
}