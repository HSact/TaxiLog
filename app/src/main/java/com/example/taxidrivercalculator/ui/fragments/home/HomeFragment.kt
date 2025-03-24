package com.example.taxidrivercalculator.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.databinding.FragmentHomeBinding
import com.example.taxidrivercalculator.ui.cards.CardLastShift

class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var textDate: TextView
    private lateinit var textEarnings: TextView
    private lateinit var textCosts: TextView
    private lateinit var textTime: TextView
    private lateinit var textTotal: TextView
    private lateinit var textPerHour: TextView
    private lateinit var textGoal: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val card1: ComposeView = binding.card1
        card1.setContent {
            CardLastShift().DrawLastShiftCard("12.12.2022")
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindItems()
        viewModel.shiftData.observe(viewLifecycleOwner) { shift ->
            textDate.text = shift["date"]
            textEarnings.text = shift["earnings"]
            textCosts.text = shift["costs"]
            textTime.text = shift["time"]
            textTotal.text = shift["total"]
            textPerHour.text = shift["perHour"]
            textGoal.text = shift["goal"]
        }
        viewModel.calculateShift(requireContext())
        binding.fabNewShift.setOnClickListener { newShift() }
    }
    override fun onResume() {
        super.onResume()
        viewModel.calculateShift(requireContext())
    }

    private fun bindItems ()
    {
        textDate = binding.textHomeDateR
        textEarnings = binding.textHomeEarningsR
        textCosts = binding.textHomeCostsR
        textTime = binding.textHomeTimeR
        textTotal = binding.textHomeTotalR
        textPerHour = binding.textHomePerHourR
        textGoal = binding.textHomeGoalR
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