package com.hsact.taxilog.di

import com.hsact.domain.repository.SettingsRepository
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.domain.usecase.settings.GetDeviceIdUseCase
import com.hsact.domain.usecase.settings.SaveAllSettingsUseCase
import com.hsact.domain.usecase.settings.UpdateSettingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SettingsUseCaseModule {

    @Provides
    fun provideGetAllSettingsUseCase(
        repository: SettingsRepository
    ): GetAllSettingsUseCase = GetAllSettingsUseCase(repository)

    @Provides
    fun provideGetDeviceIdUseCase(
        repository: SettingsRepository
    ): GetDeviceIdUseCase = GetDeviceIdUseCase(repository)

    @Provides
    fun provideUpdateSettingsUseCase(
        repository: SettingsRepository
    ): UpdateSettingUseCase = UpdateSettingUseCase(repository)

    @Provides
    fun provideUpdateSettingUseCase(
        repository: SettingsRepository
    ): SaveAllSettingsUseCase = SaveAllSettingsUseCase(repository)
}