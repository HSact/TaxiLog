package com.example.taxidrivercalculator.ui.dashboard

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taxidrivercalculator.DBHelper
import com.example.taxidrivercalculator.MainActivity
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.Shift
import com.example.taxidrivercalculator.ShiftHelper.makeArray
import com.example.taxidrivercalculator.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.*


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindItems()
        defineGoals()
        val shifts = makeArray(DBHelper(requireActivity(), null))

        if (shifts.isNullOrEmpty())
        {
            setEmptyProgress()
            return root
        }

        val yearToday = (calendar.get(Calendar.YEAR)).toString()
        var monthToday = (calendar.get(Calendar.MONTH) +1).toString()
        val weekToday = (calendar.get(Calendar.WEEK_OF_YEAR))
        val calendarSettable = Calendar.getInstance()

        if (monthToday.length==1)
        {
            monthToday = "0$monthToday"
        }

        val dateFormat = SimpleDateFormat ("dd.MM.yyyy")
        val currentDate = dateFormat.format(Date())
        var idToday = -1

        //calc today
        var i = 0
        do {
            if (shifts[i].date == currentDate)
            {
                idToday=i
                break
            }
            i++
        } while (i<shifts.size)
        if (idToday!=-1)
        {
            setTodayProgress(shifts[idToday].profit)
        }
        else
        {
            setTodayProgress(null)
        }
        if (goalMonth==-1.0)
        {
            setEmptyProgress()
            return root
        }

        //calc week
        var thisWeekSum = 0.0
        i = 0
        do {
            val thisDate = dateFormat.parse(shifts[i].date) ?: break
            calendarSettable.time = thisDate
            val thisWeek = calendarSettable.get(Calendar.WEEK_OF_YEAR)


            if (thisWeek == weekToday && thisDate.toString().contains(yearToday))
            {
                thisWeekSum += shifts[i].profit
            }
            i++
        } while (i<shifts.size)
        setWeekProgress(thisWeekSum)

        //calc month
        i = 0
        var thisMonthSum = 0.0
        do {
            val thisDateText = shifts[i].date
            if (thisDateText.indexOf(monthToday) == 3 && thisDateText.contains(yearToday))
            {
                thisMonthSum += shifts[i].profit
            }
            i++
        } while (i<shifts.size)
        setMonthProgress(thisMonthSum)

        /*val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
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