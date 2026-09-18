package com.hsact.taxilog

import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetSettingsFlowUseCase
import com.hsact.domain.usecase.shift.GetLastShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftsInRangeUseCase
import com.hsact.taxilog.ui.fragments.home.HomeViewModel
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
class HomeViewModelTest {
    private lateinit var viewModel: HomeViewModel

    private val getSettingsFlowUseCase = mock<GetSettingsFlowUseCase>()
    private val getLastShiftUseCase = mock<GetLastShiftUseCase>()
    private val getShiftsInRangeUseCase = mock<GetShiftsInRangeUseCase>()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        whenever(getSettingsFlowUseCase()).thenReturn(flowOf(UserSettings.default))
        whenever(getLastShiftUseCase()).thenReturn(flowOf(null))
        whenever(getShiftsInRangeUseCase(any(), any())).thenReturn(flowOf(emptyList()))

        viewModel = HomeViewModel(
            getSettingsFlowUseCase,
            getLastShiftUseCase,
            getShiftsInRangeUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `calculateChart handles comma in goalPerMonth`() = runTest(testDispatcher) {
        whenever(getSettingsFlowUseCase()).thenReturn(
            flowOf(
                UserSettings.default.copy(
                    isConfigured = true,
                    goalPerMonth = "50000,50"
                )
            )
        )

        // Re-init viewModel
        viewModel = HomeViewModel(
            getSettingsFlowUseCase,
            getLastShiftUseCase,
            getShiftsInRangeUseCase
        )
        val settingsJob = launch { viewModel.settings.collect {} }
        advanceUntilIdle()

        viewModel.calculateChart()

        assertEquals(50000.50, viewModel.goalData.value, 0.01)

        settingsJob.cancel()
    }
}
