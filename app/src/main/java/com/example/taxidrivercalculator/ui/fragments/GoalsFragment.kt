package com.example.taxidrivercalculator.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.taxidrivercalculator.helpers.DBHelper
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.helpers.ShiftHelper.makeArray
import com.example.taxidrivercalculator.databinding.FragmentGoalsBinding
import com.example.taxidrivercalculator.helpers.Shift
import java.text.SimpleDateFormat
import java.util.*


class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDay: ProgressBar
    private lateinit var progressWeek: ProgressBar
    private lateinit var progressMonth: ProgressBar

    private lateinit var todayPercent: TextView
    private lateinit var weekPercent: TextView
    private lateinit var monthPercent: TextView
    private lateinit var textAssignedGoal: TextView
    private var goalMonth: Double = -1.0
    private var goalWeek: Double = -1.0
    private var goalDay: Double = -1.0

    private val calendar = Calendar.getInstance()

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
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

    private fun calculateDayProgress(): Double
    {
        val shifts = makeArray(DBHelper(requireActivity(), null))
        val currentDate = getDayToday()
        var idToday = -1

        var i = 0
        do {
            if (shifts[i].date == currentDate) {
                idToday = i
                break
            }
            i++
        } while (i < shifts.size)
        if (idToday != -1)
        {
            return shifts[idToday].profit
        }
        else
        {
            return 0.0
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun calculateWeekProgress(): Double
    {
        val calendarSettable = Calendar.getInstance()
        val shifts = makeArray(DBHelper(requireActivity(), null))
        var thisWeekSum = 0.0
        var i = 0
        val dateFormat = SimpleDateFormat ("dd.MM.yyyy")
        do
        {
            val thisDate = dateFormat.parse(shifts[i].date) ?: break
            calendarSettable.time = thisDate
            val thisWeek = calendarSettable.get(Calendar.WEEK_OF_YEAR)
            if (thisWeek == getWeekToday() && thisDate.toString().contains(getYearToday())) {
                thisWeekSum += shifts[i].profit
            }
            i++
        } while (i < shifts.size)
        return thisWeekSum
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDayToday(): String {
        val dateFormat = SimpleDateFormat ("dd.MM.yyyy")
        val currentDate = dateFormat.format(Date())
        return currentDate
    }

    private fun getWeekToday(): Int {
        val weekToday = (calendar.get(Calendar.WEEK_OF_YEAR))
        return weekToday
    }

    private fun getMonthToday(): String {
        var monthToday = (calendar.get(Calendar.MONTH) + 1).toString()
        if (monthToday.length==1)
        {
            monthToday = "0$monthToday"
        }
        return monthToday
    }

    private fun getYearToday(): String {
        val yearToday = (calendar.get(Calendar.YEAR)).toString()
        return yearToday
    }

    private fun calculateMonthProgress(): Double
    {
        val shifts = makeArray(DBHelper(requireActivity(), null))
        var i = 0
        var thisMonthSum = 0.0
        do {
            val thisDateText = shifts[i].date
            if (thisDateText.indexOf(getMonthToday()) == 3 && thisDateText.contains(getYearToday())) {
                thisMonthSum += shifts[i].profit
            }
            i++
        } while (i < shifts.size)
        return thisMonthSum
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


    }

    private fun displayMonthGoal(goalMonthString: String) {
        textAssignedGoal.text = getString(R.string.your_goal_per_month, goalMonthString)
    }

    fun setAllProgress ()
    {

        setTodayProgress(calculateDayProgress())
        setWeekProgress(calculateWeekProgress())
        setMonthProgress(calculateMonthProgress())
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
    }

    @SuppressLint("SetTextI18n")
    private fun setWeekProgress(thisWeekSum: Double) {
        val goal = goalWeek
        val progress = (thisWeekSum/goal*100).toInt()
        weekPercent.text = "$progress%"
        progressWeek.progress = progress
    }

    @SuppressLint("SetTextI18n")
    private fun setMonthProgress(thisMonthSum: Double) {
        val goal = goalMonth
        val progress = (thisMonthSum/goal*100).toInt()
        monthPercent.text = "$progress%"
        progressMonth.progress = progress
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
        progressDay = binding.progressDay
        progressWeek = binding.progressWeek
        progressMonth = binding.progressMonth

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