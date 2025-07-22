package com.hsact.taxilog

import com.hsact.data.sync.ShiftSyncManager
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.model.settings.UserSettings
import com.hsact.domain.usecase.settings.GetAllSettingsUseCase
import com.hsact.domain.usecase.settings.GetDeviceIdUseCase
import com.hsact.domain.usecase.shift.AddShiftUseCase
import com.hsact.domain.usecase.shift.GetShiftByIdUseCase
import com.hsact.taxilog.ui.fragments.shiftForm.ShiftFormViewModel
import com.hsact.taxilog.ui.fragments.shiftForm.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
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

    private val getAllSettingsUseCase = mock<GetAllSettingsUseCase>()
    private val getDeviceIdUseCase = mock<GetDeviceIdUseCase>()
    private val addShiftUseCase = mock<AddShiftUseCase>()
    private val getShiftByIdUseCase = mock<GetShiftByIdUseCase>()
    private val shiftSyncManager = mock<ShiftSyncManager>()

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        whenever(getAllSettingsUseCase()).thenReturn(
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
                fuelPrice = "65",
            )
        )

        viewModel = ShiftFormViewModel(
            getAllSettingsUseCase,
            getDeviceIdUseCase,
            addShiftUseCase,
            getShiftByIdUseCase,
            shiftSyncManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `guessFuelCost calculates correctly`() {
        val initial = UiState(
            mileage = 100.0 // 100 км
        )
        viewModel.updateShift(initial)

        viewModel.guessFuelCost()

        val updated = viewModel.uiState.getOrAwaitValue()
        assertEquals(15.0, updated.fuelCost, 0.01) // 1.5 * 100 * 10 / 100 = 15.0
    }
}