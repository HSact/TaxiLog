package com.hsact.taxilog.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.FragmentHomeBinding
import com.hsact.taxilog.ui.cards.DrawGoalCard
import com.hsact.taxilog.ui.cards.DrawLastShiftCard
import com.hsact.taxilog.ui.cards.DrawMonthGraphCard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var card1: ComposeView
    private lateinit var card2: ComposeView
    private lateinit var card3: ComposeView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        card1 = binding.card1
        card2 = binding.card2
        card3 = binding.card3
        card1.setContent { DrawGoalCard(viewModel.settings.goalPerMonth?.toFloatOrNull() ?: 0f,
            viewModel.shiftListThisMonth) }
        card2.setContent { DrawLastShiftCard(viewModel.lastShift) }
        card3.setContent { DrawMonthGraphCard(viewModel.chartData, viewModel.goalData) }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.calculateShift(requireContext())
        viewModel.calculateChart(requireContext())
        binding.fabNewShift.setOnClickListener { newShift() }
    }
    override fun onResume() {
        super.onResume()
        viewModel.calculateShift(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun newShift()
    {
        findNavController().navigate(R.id.action_homeFragment_to_addShift)
    }
}