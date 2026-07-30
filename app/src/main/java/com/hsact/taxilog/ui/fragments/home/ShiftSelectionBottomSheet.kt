package com.hsact.taxilog.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.domain.usecase.shift.GetShiftSequenceNumberUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Bottom sheet that allows the user to select one shift from a list of shifts.
 * This is used when a calendar day has multiple shifts.
 * Data is retrieved from the parent Fragment's HomeViewModel to ensure persistence.
 */
@AndroidEntryPoint
class ShiftSelectionBottomSheet : BottomSheetDialogFragment() {
    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })

    @Inject
    lateinit var getShiftSequenceNumberUseCase: GetShiftSequenceNumberUseCase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                val shifts by viewModel.shiftsForSelection.collectAsStateWithLifecycle()
                val settings by viewModel.settings.collectAsStateWithLifecycle()
                val locale = LocalConfiguration.current.locales[0]
                val currency =
                    settings.currency
                        ?: CurrencySymbolMode.fromLocale(locale)

                ShiftSelectionContent(
                    shifts = shifts,
                    currencySymbolMode = currency,
                    getShiftSequenceNumberUseCase = getShiftSequenceNumberUseCase,
                    onShiftSelected = {
                        (parentFragment as? HomeFragment)?.navigateToShiftDetail(it.id)
                        dismiss()
                    },
                )
            }
        }

    companion object {
        const val TAG = "ShiftSelectionBottomSheet"
    }
}
