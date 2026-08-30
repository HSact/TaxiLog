package com.hsact.taxilog.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLocale
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.hsact.domain.model.Shift
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.FragmentHomeBinding
import com.hsact.taxilog.ui.cards.CardCalendar
import com.hsact.taxilog.ui.cards.CardGoal
import com.hsact.taxilog.ui.cards.CardGoalProgress
import com.hsact.taxilog.ui.cards.CardLastShift
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardGoal: ComposeView
    private lateinit var cardCalendar: ComposeView
    private lateinit var cardLastShift: ComposeView
    private lateinit var cardMonthGraph: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        cardGoal = binding.cardGoal
        cardCalendar = binding.cardCalendar
        cardLastShift = binding.cardLastShift
        cardMonthGraph = binding.cardGraph
        cardGoal.setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            CardGoal(
                settings.goalPerMonth?.toFloatOrNull() ?: 0f,
                viewModel.shiftListThisMonth,
                onSetGoalClick = {
                    findNavController().navigate(R.id.settingsFragment)
                },
            )
        }
        cardCalendar.setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            CardCalendar(
                viewModel.calendarMonth,
                viewModel.calendarShifts,
                settings.firstDayOfWeek,
                { viewModel.onPreviousMonth() },
                { viewModel.onNextMonth() },
            ) { shifts ->
                handleShiftsClick(shifts)
            }
        }
        cardLastShift.setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoadingLastShift.collectAsStateWithLifecycle()
            CardLastShift(
                viewModel.lastShift,
                isLoading,
                settings.currency ?: CurrencySymbolMode.fromLocale(LocalLocale.current.platformLocale),
            ) {
                val action =
                    HomeFragmentDirections.actionNavigationHomeToShiftDetails(
                        shiftId = viewModel.lastShift.value?.id ?: -1,
                    )
                findNavController().navigate(action)
            }
        }
        cardMonthGraph.setContent { CardGoalProgress(viewModel.chartData, viewModel.goalData) }
        binding.fabNewShift.extend()
        binding.fabNewShift.setOnClickListener { newShift() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun newShift() {
        val action =
            HomeFragmentDirections
                .actionHomeFragmentToAddShift(
                    shiftId = -1,
                )
        findNavController().navigate(action)
    }

    /**
     * Handles clicks on calendar days. If a day has multiple shifts, shows a selection bottom sheet.
     */
    private fun handleShiftsClick(shifts: List<Shift>) {
        if (shifts.isEmpty()) return

        if (shifts.size == 1) {
            navigateToShiftDetail(shifts.first().id)
        } else {
            viewModel.selectShifts(shifts)
            val bottomSheet = ShiftSelectionBottomSheet()
            bottomSheet.show(childFragmentManager, ShiftSelectionBottomSheet.TAG)
        }
    }

    /**
     * Navigates to the shift detail screen.
     */
    fun navigateToShiftDetail(shiftId: Int) {
        val action =
            HomeFragmentDirections.actionNavigationHomeToShiftDetails(
                shiftId = shiftId,
            )
        findNavController().navigate(action)
    }
}
