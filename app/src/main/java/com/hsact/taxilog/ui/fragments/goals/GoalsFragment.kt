package com.hsact.taxilog.ui.fragments.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hsact.domain.utils.DeprecatedDateFormatter
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.FragmentGoalsBinding
import com.hsact.taxilog.ui.cards.DrawDayWeekMonthProgressCard
import com.hsact.taxilog.ui.cards.CardDaysInMonth
import com.hsact.taxilog.ui.components.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalsViewModel by viewModels()

    private lateinit var card1: ComposeView
    private lateinit var card2: ComposeView

    private lateinit var buttonDatePicker: EditText

    private lateinit var textAssignedGoal: TextView

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
        displayMonthGoal(viewModel.goalMonthString)
        card1.setContent {
            DrawDayWeekMonthProgressCard(viewModel.goalDataState)
        }
        card2.setContent {
            CardDaysInMonth(viewModel.daysInMonthCardState)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun pickDate(editObj: EditText) {
        DatePickerFragment.pickDate(context = this, editObj = editObj) {
            viewModel.setDate(editObj.text.toString())
            card1.setContent { DrawDayWeekMonthProgressCard(viewModel.goalDataState) }
            card2.setContent { CardDaysInMonth(viewModel.daysInMonthCardState) }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.defineGoals()
    }

    private fun displayMonthGoal(goalMonthString: String?) {
        if (goalMonthString.isNullOrEmpty())
        {
            textAssignedGoal.text = getString(R.string.you_haven_t_set_goal_yet)
            buttonDatePicker.isGone = true
            card1.isGone = true
            card2.isGone = true
            return
        }
        textAssignedGoal.isGone = true
        buttonDatePicker.isVisible = true
        card1.isVisible = true
        card2.isVisible = true
    }

    private fun bindItems() {
        buttonDatePicker = binding.buttonDatePick
        textAssignedGoal = binding.textAssignedGoal
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}