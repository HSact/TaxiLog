package com.hsact.di

import android.content.Context
import androidx.room.Room
import com.hsact.data.db.AppDatabase
import com.hsact.data.db.ShiftDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "your_database_name"
        ).build()
    }

    @Provides
    @Singleton
    fun provideShiftDao(db: AppDatabase): ShiftDao = db.shiftDao()
}