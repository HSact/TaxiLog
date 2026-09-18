package com.hsact.taxilog

import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetSettingsFlowUseCase
import com.hsact.domain.usecase.shift.GetShiftsInRangeUseCase
import com.hsact.taxilog.ui.fragments.goals.GoalsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class GoalsViewModelTest {
    private lateinit var viewModel: GoalsViewModel

    private val getSettingsFlowUseCase = mock<GetSettingsFlowUseCase>()
    private val getShiftsInRangeUseCase = mock<GetShiftsInRangeUseCase>()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        whenever(getSettingsFlowUseCase()).thenReturn(
            flowOf(
                UserSettings.default.copy(
                    isConfigured = true,
                    goalPerMonth = "100000",
                    schedule = "6/1"
                )
            )
        )
        whenever(getShiftsInRangeUseCase(any(), any())).thenReturn(flowOf(emptyList()))

        viewModel = GoalsViewModel(getSettingsFlowUseCase, getShiftsInRangeUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `defineGoals handles comma in goalPerMonth`() = runTest(testDispatcher) {
        whenever(getSettingsFlowUseCase()).thenReturn(
            flowOf(
                UserSettings.default.copy(
                    isConfigured = true,
                    goalPerMonth = "123456,78",
                    schedule = "7/0"
                )
            )
        )

        // Re-init viewModel
        viewModel = GoalsViewModel(getSettingsFlowUseCase, getShiftsInRangeUseCase)
        val settingsJob = launch { viewModel.settings.collect {} }
        advanceUntilIdle()

        // goalDataState should be updated
        val goalData = viewModel.goalDataState.value
        assertEquals(123456.78, goalData.monthGoal, 0.01)

        settingsJob.cancel()
    }
}
