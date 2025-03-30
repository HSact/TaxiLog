package com.example.taxidrivercalculator.ui.fragments.goals

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.databinding.FragmentGoalsBinding
import com.example.taxidrivercalculator.ui.cards.CardDayGoal
import com.example.taxidrivercalculator.ui.fragments.DatePickerFragment
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalsViewModel by viewModels()

    private lateinit var tableProgress: TableLayout

    private lateinit var card1: ComposeView
    private lateinit var card2: ComposeView
    private lateinit var card3: ComposeView

    private lateinit var progressDay: ProgressBar
    private lateinit var progressWeek: ProgressBar
    private lateinit var progressMonth: ProgressBar

    private lateinit var buttonDatePicker: EditText

    private lateinit var todayPercent: TextView
    private lateinit var weekPercent: TextView
    private lateinit var monthPercent: TextView
    private lateinit var textAssignedGoal: TextView

    private lateinit var dayName: TextView
    private lateinit var weekName: TextView
    private lateinit var monthName: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindItems()
        card1 = binding.card1
        card1.setContent { CardDayGoal().DrawDayGoalCard(viewModel.daysData) }
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
        viewModel.defineGoals(viewModel.pickedDate, requireContext())
        viewModel.calculateDaysData(viewModel.pickedDate, requireContext())
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.goalData.observe(viewLifecycleOwner) {
            setTodayProgress(it["todayPercent"])
            setWeekProgress(it["weekPercent"])
            setMonthProgress(it["monthPercent"])
        }
        displayMonthGoal(viewModel.goalMonthString)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun pickDate(editObj: EditText) {
        DatePickerFragment.pickDate(context = requireContext(), editObj = editObj) {
            viewModel.pickedDate = editObj.text.toString()
            viewModel.defineGoals(viewModel.pickedDate, requireContext())
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
        viewModel.defineGoals(viewModel.pickedDate, requireContext())
    }

    private fun displayMonthGoal(goalMonthString: String?) {
        if (goalMonthString.isNullOrEmpty())
        {
            textAssignedGoal.text = getString(R.string.you_haven_t_set_goal_yet)
            tableProgress.isGone = true
            return
        }
        textAssignedGoal.text = getString(R.string.your_goal_per_month, goalMonthString)
        tableProgress.isVisible = true
    }

    @SuppressLint("SetTextI18n")
    fun setTodayProgress(progress: Double?) {
        if (progress == null)
        {
            progressDay.progress = 0
            todayPercent.text = "0%"
            return
        }
        todayPercent.text = "$progress%"
        progressDay.progress = progress.toInt()
        //dayName.text=getCurrentDay()
    }

    @SuppressLint("SetTextI18n")
    private fun setWeekProgress(progress: Double?) {
        if (progress == null)
        {
            progressWeek.progress = 0
            weekPercent.text = "0%"
            return
        }
        weekPercent.text = "$progress%"
        progressWeek.progress = progress.toInt()
        //weekName.text=getCurrentWeek().toString()
    }

    @SuppressLint("SetTextI18n")
    private fun setMonthProgress(progress: Double?) {
        if (progress == null)
        {
            progressMonth.progress = 0
            monthPercent.text = "0%"
            return
        }
        monthPercent.text = "$progress%"
        progressMonth.progress = progress.toInt()
        //monthName.text=getCurrentMonth()
    }

    private fun bindItems()
    {
        tableProgress = binding.tableProgress
        progressDay = binding.progressDay
        progressWeek = binding.progressWeek
        progressMonth = binding.progressMonth
        dayName = binding.textGoalPerDay
        weekName = binding.textGoalPerWeek
        monthName = binding.textGoalPerMonth
        buttonDatePicker = binding.buttonDatePick
        todayPercent = binding.textTodayPercent
        weekPercent = binding.textWeekPercent
        monthPercent = binding.textMontPercent
        textAssignedGoal = binding.textAssignedGoal
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}