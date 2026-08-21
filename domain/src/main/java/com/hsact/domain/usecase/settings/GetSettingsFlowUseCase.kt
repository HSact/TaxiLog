package com.hsact.domain.usecase.settings

import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to retrieve a reactive stream of the application settings.
 *
 * This allows the UI components to automatically update whenever the user
 * changes settings in the Settings screen.
 */
class GetSettingsFlowUseCase(
    private val repository: SettingsRepository,
) {
    /**
     * Invokes the use case to get the settings flow.
     * @return A [Flow] of [UserSettings].
     */
    operator fun invoke(): Flow<UserSettings> = repository.getSettingsFlow()
}
