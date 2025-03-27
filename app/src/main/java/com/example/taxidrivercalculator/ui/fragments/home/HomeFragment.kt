package com.example.taxidrivercalculator.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.databinding.FragmentHomeBinding
import com.example.taxidrivercalculator.ui.cards.CardGoal
import com.example.taxidrivercalculator.ui.cards.CardLastShift
import com.example.taxidrivercalculator.ui.cards.CardMonthGraph

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
        card1.setContent { CardGoal().DrawGoalCard(viewModel.shiftData) }
        card2.setContent { CardLastShift().DrawLastShiftCard(viewModel.shiftData) }
        card3.setContent { CardMonthGraph().DrawMonthGraphCard(viewModel.chartData, viewModel.goalData) }
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