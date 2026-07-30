package com.hsact.taxilog.ui.fragments.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.hsact.domain.utils.DeprecatedDateFormatter
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.FragmentGoalsBinding
import com.hsact.taxilog.ui.AppTheme
import com.hsact.taxilog.ui.cards.CardDayWeekMonthProgress
import com.hsact.taxilog.ui.cards.CardDaysInMonth
import com.hsact.taxilog.ui.components.DatePickerFragment
import com.hsact.taxilog.ui.components.EmptyStateView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@AndroidEntryPoint
class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalsViewModel by viewModels()

    private lateinit var card1: ComposeView
    private lateinit var card2: ComposeView

    private lateinit var buttonDatePicker: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindItems()
        card1 = binding.card1
        card2 = binding.card2

        binding.emptyStateCompose.setContent {
            AppTheme {
                EmptyStateView(
                    icon = Icons.Default.Info,
                    title = getString(R.string.goals_empty_title),
                    description = getString(R.string.goals_empty_description),
                    actionText = getString(R.string.settings_label),
                    onAction = {
                        findNavController().navigate(R.id.settingsFragment)
                    }
                )
            }
        }

        val currentDate = LocalDateTime.now().toLocalDate()
        buttonDatePicker.setText(currentDate.format(DeprecatedDateFormatter))
        buttonDatePicker.setOnClickListener {
            pickDate(buttonDatePicker)
        }
        viewModel.defineGoals()
        viewModel.calculateDaysData()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goalMonthString.collect { goal ->
                    displayMonthGoal(goal)
                }
            }
        }
        card1.setContent {
            CardDayWeekMonthProgress(viewModel.goalDataState)
        }
        card2.setContent {
            CardDaysInMonth(viewModel.daysInMonthCardState)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun pickDate(editObj: EditText) {
        DatePickerFragment.pickDate(context = this, editObj = editObj) {
            viewModel.setDate(editObj.text.toString())
            card1.setContent { CardDayWeekMonthProgress(viewModel.goalDataState) }
            card2.setContent { CardDaysInMonth(viewModel.daysInMonthCardState) }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.defineGoals()
    }

    private fun displayMonthGoal(goalMonthString: String?) {
        val isEmpty = goalMonthString.isNullOrEmpty()
        
        binding.emptyStateCompose.isVisible = isEmpty
        buttonDatePicker.isVisible = !isEmpty
        card1.isVisible = !isEmpty
        card2.isVisible = !isEmpty
    }

    private fun bindItems() {
        buttonDatePicker = binding.buttonDatePick
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}