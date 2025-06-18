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
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.FragmentGoalsBinding
import com.hsact.taxilog.ui.cards.DrawDayWeekMonthProgressCard
import com.hsact.taxilog.ui.cards.DrawDaysInMonthCard
import com.hsact.taxilog.ui.components.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
        card1.setContent { DrawDayWeekMonthProgressCard(viewModel.goalData) }
        card2.setContent { DrawDaysInMonthCard(viewModel.daysData, viewModel.pickedDate) }
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val now = LocalDateTime.now()
        val currentDate = now.toLocalDate()
        if (viewModel.pickedDate.isEmpty())
        {
            viewModel.pickedDate = getCurrentDay()
        }
        buttonDatePicker.setText(currentDate.format(formatter))
        buttonDatePicker.setOnClickListener {
            pickDate(buttonDatePicker)
        }
        viewModel.defineGoals(viewModel.pickedDate)
        viewModel.calculateDaysData(viewModel.pickedDate)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        displayMonthGoal(viewModel.goalMonthString)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun pickDate(editObj: EditText) {
        DatePickerFragment.pickDate(context = this, editObj = editObj) {
            viewModel.pickedDate = editObj.text.toString()
            viewModel.calculateDaysData(viewModel.pickedDate)
            viewModel.defineGoals(viewModel.pickedDate)
            card1.setContent { DrawDayWeekMonthProgressCard(viewModel.goalData) }
            card2.setContent { DrawDaysInMonthCard(viewModel.daysData, viewModel.pickedDate) }
        }
    }

    private fun getCurrentDay(): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        return try {
            buttonDatePicker.text.takeIf { it.isNotEmpty() }
                ?.let { LocalDate.parse(it, dateFormatter).format(dateFormatter) }
                ?: LocalDate.now().format(dateFormatter)
        } catch (e: DateTimeParseException) {
            LocalDate.now().format(dateFormatter)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.defineGoals(viewModel.pickedDate)
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

    private fun bindItems()
    {
        buttonDatePicker = binding.buttonDatePick
        textAssignedGoal = binding.textAssignedGoal
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}