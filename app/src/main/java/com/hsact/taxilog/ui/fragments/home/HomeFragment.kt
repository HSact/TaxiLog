package com.hsact.taxilog.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hsact.domain.model.settings.CurrencySymbolMode
import com.hsact.taxilog.databinding.FragmentHomeBinding
import com.hsact.taxilog.ui.cards.CardGoal
import com.hsact.taxilog.ui.cards.CardLastShift
import com.hsact.taxilog.ui.cards.MonthGraphCard
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardGoal: ComposeView
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardGoal = binding.card1
        cardLastShift = binding.card2
        cardMonthGraph = binding.card3
        cardGoal.setContent {
            CardGoal(
                viewModel.settings.goalPerMonth?.toFloatOrNull() ?: 0f,
                viewModel.shiftListThisMonth
            )
        }
        cardLastShift.setContent {
            CardLastShift(
                viewModel.lastShift,
                viewModel.settings.currency ?: CurrencySymbolMode.fromLocale(Locale.getDefault())
            ) {
                val action = HomeFragmentDirections.actionNavigationHomeToShiftDetails(
                    shiftId = viewModel.lastShift.value?.id ?: -1,
                    visibleId = -1
                )
                findNavController().navigate(action)
            }
        }
        cardMonthGraph.setContent { MonthGraphCard(viewModel.chartData, viewModel.goalData) }
        binding.fabNewShift.extend()
        binding.fabNewShift.setOnClickListener { newShift() }

        cardLastShift.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToShiftDetails(
                shiftId = viewModel.lastShift.value?.id ?: -1,
                visibleId = -1
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun newShift() {
        val action = HomeFragmentDirections
            .actionHomeFragmentToAddShift(
                shiftId = -1,
                visibleId = -1
            )
        findNavController().navigate(action)
    }
}