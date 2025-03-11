package com.example.taxidrivercalculator.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.taxidrivercalculator.helpers.DBHelper
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.helpers.ShiftHelper.makeArray
import com.example.taxidrivercalculator.databinding.FragmentGoalsBinding
import com.example.taxidrivercalculator.helpers.ShiftHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var tableProgress: TableLayout

    private lateinit var progressDay: ProgressBar
    private lateinit var progressWeek: ProgressBar
    private lateinit var progressMonth: ProgressBar

    private lateinit var buttonDatePicker: EditText
    private var pickedDate: String = ""

    private lateinit var todayPercent: TextView
    private lateinit var weekPercent: TextView
    private lateinit var monthPercent: TextView
    private lateinit var textAssignedGoal: TextView
    private var goalMonth: Double = -1.0
    private var goalWeek: Double = -1.0
    private var goalDay: Double = -1.0
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
        defineGoals()
        val shifts = makeArray(DBHelper(requireActivity(), null))
        if (shifts.isNullOrEmpty() || goalMonth == -1.0)
        {
            setEmptyProgress()
            return root
        }
        setAllProgress ()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val now = LocalDateTime.now()
        val currentDate = now.toLocalDate()
        pickedDate = getCurrentDay()
        buttonDatePicker.setText(currentDate.format(formatter))
        buttonDatePicker.setOnClickListener {
            pickDate(buttonDatePicker)
            //setAllProgress ()
        }
        //val textView: TextView = binding.textDashboard
        /*val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    override fun onStart() {
        defineGoals()
        val shifts = makeArray(DBHelper(requireActivity(), null))
        if (shifts.isNullOrEmpty() || goalMonth == -1.0)
        {
            setEmptyProgress()
        }
        else {
        setAllProgress()
        }
        super.onStart()
    }

    private fun pickDate(editObj: EditText)
    {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.selectedDate = editObj.text.toString()
        val supportFragmentManager = requireActivity().supportFragmentManager
        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner)
        { resultKey, bundle ->
            if (resultKey == "REQUEST_KEY") {
                val date = bundle.getString("SELECTED_DATE")
                editObj.setText(date.toString())
                pickedDate = date.toString()
                setAllProgress ()
            }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
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
        animateBars()
    }

    private fun defineGoals() {
        val settings = this.activity?.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val goalMonthString = settings?.getString("Goal_per_month", "")
        if (goalMonthString == null || goalMonthString == "" || goalMonthString == "-1")
        {
            textAssignedGoal.text = getString(R.string.you_haven_t_set_goal_yet)
            setEmptyProgress()
            buttonDatePicker.isGone
            tableProgress.isGone
            return
        }
        displayMonthGoal(goalMonthString)
        goalMonth = goalMonthString.toDouble()
        val schedule = settings.getString("Schedule_text", "")
        var denominatorDay = 1.0
        val denominatorWeek = 4.5
        goalWeek = goalMonth / denominatorWeek
        when (schedule)
        {
            "7/0" -> denominatorDay = 30.0
            "6/1" -> denominatorDay = 25.7
            "5/2" -> denominatorDay = 21.4
        }
        goalDay = goalMonth / denominatorDay
        buttonDatePicker.isVisible
        tableProgress.isVisible
    }

    private fun displayMonthGoal(goalMonthString: String) {
        textAssignedGoal.text = getString(R.string.your_goal_per_month, goalMonthString)
    }

    private fun setAllProgress ()
    {
        setTodayProgress(ShiftHelper.calculateDayProgress(buttonDatePicker.text.toString(), DBHelper(requireActivity(), null)))
        setWeekProgress(ShiftHelper.calculateWeekProgress(buttonDatePicker.text.toString(), DBHelper(requireActivity(), null)))
        setMonthProgress(ShiftHelper.calculateMonthProgress(buttonDatePicker.text.toString(), DBHelper(requireActivity(), null)))
    }

    @SuppressLint("SetTextI18n")
    fun setTodayProgress(todayProfit: Double?) {
        if (todayProfit == null || goalDay == -1.0)
        {
            progressDay.progress = 0
            todayPercent.text = "0%"
            return
        }
        val goal = goalDay
        val progress = (todayProfit/goal*100).toInt()
        todayPercent.text = "$progress%"
        progressDay.progress = progress
        //dayName.text=getCurrentDay()
    }

    @SuppressLint("SetTextI18n")
    private fun setWeekProgress(thisWeekSum: Double) {
        val goal = goalWeek
        val progress = (thisWeekSum/goal*100).toInt()
        weekPercent.text = "$progress%"
        progressWeek.progress = progress
        //weekName.text=getCurrentWeek().toString()
    }

    @SuppressLint("SetTextI18n")
    private fun setMonthProgress(thisMonthSum: Double) {
        val goal = goalMonth
        val progress = (thisMonthSum/goal*100).toInt()
        monthPercent.text = "$progress%"
        progressMonth.progress = progress
        //monthName.text=getCurrentMonth()
    }

    private fun setEmptyProgress() {
        todayPercent.text = "0%"
        weekPercent.text = "0%"
        monthPercent.text = "0%"
        progressDay.progress = 0
        progressWeek.progress = 0
        progressMonth.progress = 0
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
    private fun animateBars()
    {
        /*val animDuration: Long = 1000
        progressDay.setProgress(0, false)
        progressWeek.setProgress(0, false)
        progressMonth.setProgress(0, false)
        lateinit var anim: Animation
        anim = AnimationUtils.loadAnimation(requireContext(),0)
        anim.duration = animDuration
        progressDay.startAnimation(anim)
        //progressDay.setProgress(75, true)*/
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}