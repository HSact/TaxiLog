package com.hsact.di

import com.hsact.domain.repository.ShiftRepository
import com.hsact.domain.usecase.shift.AddShiftUseCase
import com.hsact.domain.usecase.shift.DeleteAllShiftsUseCase
import com.hsact.domain.usecase.shift.DeleteShiftUseCase
import com.hsact.domain.usecase.shift.GetAllShiftsUseCase
import com.hsact.domain.usecase.shift.GetLastShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftByIdUseCase
import com.hsact.domain.usecase.shift.GetShiftSequenceNumberUseCase
import com.hsact.domain.usecase.shift.GetShiftsInRangeUseCase
import com.hsact.domain.usecase.shift.SyncShiftsUseCase
import com.hsact.domain.usecase.shift.UpdateShiftUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ShiftUseCaseModule {
    @Provides
    fun provideSyncShiftsUseCase(repository: ShiftRepository): SyncShiftsUseCase = SyncShiftsUseCase(repository)

    @Provides
    fun provideGetAllShiftsUseCase(repository: ShiftRepository): GetAllShiftsUseCase = GetAllShiftsUseCase(repository)

    @Provides
    fun provideGetShiftByIdUseCase(repository: ShiftRepository): GetShiftByIdUseCase = GetShiftByIdUseCase(repository)

    @Provides
    fun provideGetShiftSequenceNumberUseCase(repository: ShiftRepository): GetShiftSequenceNumberUseCase =
        GetShiftSequenceNumberUseCase(repository)

    @Provides
    fun provideAddShiftUseCase(repository: ShiftRepository): AddShiftUseCase = AddShiftUseCase(repository)

    @Provides
    fun provideDeleteShiftUseCase(repository: ShiftRepository): DeleteShiftUseCase = DeleteShiftUseCase(repository)

    @Provides
    fun provideUpdateShiftUseCase(repository: ShiftRepository): UpdateShiftUseCase = UpdateShiftUseCase(repository)

    @Provides
    fun provideDeleteAllShiftsUseCase(repository: ShiftRepository): DeleteAllShiftsUseCase =
        DeleteAllShiftsUseCase(repository)

    @Provides
    fun provideGetLastShiftUseCase(repository: ShiftRepository): GetLastShiftUseCase = GetLastShiftUseCase(repository)

    @Provides
    fun provideGetShiftsInRangeUseCase(repository: ShiftRepository): GetShiftsInRangeUseCase =
        GetShiftsInRangeUseCase(repository)
}
