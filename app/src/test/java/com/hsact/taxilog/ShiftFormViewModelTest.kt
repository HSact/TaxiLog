package com.hsact.taxilog

import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetSettingsFlowUseCase
import com.hsact.domain.usecase.settings.GetDeviceIdUseCase
import com.hsact.domain.usecase.shift.AddShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftByIdUseCase
import com.hsact.domain.usecase.shift.GetShiftSequenceNumberUseCase
import com.hsact.taxilog.ui.fragments.shiftForm.ShiftFormViewModel
import com.hsact.taxilog.ui.fragments.shiftForm.UiState
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
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ShiftFormViewModelTest {
    private lateinit var viewModel: ShiftFormViewModel

    private val getSettingsFlowUseCase = mock<GetSettingsFlowUseCase>()
    private val getDeviceIdUseCase = mock<GetDeviceIdUseCase>()
    private val addShiftUseCase = mock<AddShiftUseCase>()
    private val getShiftByIdUseCase = mock<GetShiftByIdUseCase>()
    private val getShiftSequenceNumberUseCase = mock<GetShiftSequenceNumberUseCase>()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        whenever(getSettingsFlowUseCase()).thenReturn(
            flowOf(
                UserSettings(
                    isConfigured = true,
                    language = "en",
                    theme = "dark",
                    currency = CurrencySymbolMode.USD,
                    isKmUnit = true,
                    consumption = "10",
                    rented = false,
                    rentCost = "1000",
                    service = false,
                    serviceCost = null,
                    goalPerMonth = "120000",
                    schedule = null,
                    taxes = false,
                    taxRate = "5",
                    fuelPrice = "1.5",
                ),
            ),
        )

        viewModel =
            ShiftFormViewModel(
                getSettingsFlowUseCase,
                getDeviceIdUseCase,
                addShiftUseCase,
                getShiftByIdUseCase,
                getShiftSequenceNumberUseCase,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guessFuelCost calculates correctly`() = runTest(testDispatcher) {
        val settingsJob = launch { viewModel.settings.collect {} }
        val initial =
            UiState(
                mileage = 100.0, // 100 km
            )
        viewModel.updateShift(initial)
        advanceUntilIdle()

        // Verify settings are loaded
        assertEquals(true, viewModel.settings.value.isConfigured)

        viewModel.guessFuelCost()

        val updated = viewModel.uiState.value
        assertEquals(15.0, updated.fuelCost, 0.01)
        settingsJob.cancel()
    }

    @Test
    fun `guessFuelCost handles comma in settings`() = runTest(testDispatcher) {
        whenever(getSettingsFlowUseCase()).thenReturn(
            flowOf(
                UserSettings(
                    isConfigured = true,
                    language = "ru",
                    theme = "dark",
                    currency = CurrencySymbolMode.RUB,
                    isKmUnit = true,
                    consumption = "10,5",
                    rented = false,
                    rentCost = "1000",
                    service = false,
                    serviceCost = null,
                    goalPerMonth = "120000",
                    schedule = null,
                    taxes = false,
                    taxRate = "5",
                    fuelPrice = "74,77",
                ),
            ),
        )

        // Re-init viewModel to pick up new settings flow
        viewModel = ShiftFormViewModel(
            getSettingsFlowUseCase,
            getDeviceIdUseCase,
            addShiftUseCase,
            getShiftByIdUseCase,
            getShiftSequenceNumberUseCase
        )
        val settingsJob = launch { viewModel.settings.collect {} }

        val initial = UiState(mileage = 100.0)
        viewModel.updateShift(initial)
        advanceUntilIdle()

        viewModel.guessFuelCost()

        val updated = viewModel.uiState.value
        // formula: fuelPrice * mileage * consumption / 100
        // 74.77 * 100 * 10.5 / 100 = 74.77 * 10.5 = 785.085
        // centsRound rounds to 2 decimal places: 785.09
        assertEquals(785.09, updated.fuelCost, 0.01)
        settingsJob.cancel()
    }
}
