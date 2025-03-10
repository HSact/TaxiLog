package com.example.taxidrivercalculator.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taxidrivercalculator.helpers.DBHelper
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.databinding.FragmentHomeBinding
import com.example.taxidrivercalculator.helpers.ShiftHelper

class HomeFragment : Fragment() {

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
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindItems()
        printShift()
        binding.buttonNewShift.setOnClickListener { newShift() }
    }

    @SuppressLint("SetTextI18n")
    private fun printShift ()
    {
        val db = DBHelper(requireActivity(), null)
        //db.addShift("1.01.1001", 8.0, 1337.0, 228.0, 1488.0, 30.0, 300.0)
        val cursor = db.getShift()
        cursor!!.moveToLast()
        if (cursor.position==-1)
        {
            setEmptyView()
            cursor.close()
            return
        }
        val shifts = ShiftHelper.makeArray(db)

        textDate.text = cursor.getString(cursor.getColumnIndex(DBHelper.DATE_COl)+0)
        textEarnings.text = cursor.getString(cursor.getColumnIndex(DBHelper.EARNINGS_COL)+0)
        textCosts.text = ((cursor.getDouble(cursor.getColumnIndex(DBHelper.WASH_COL)+0))+
                (cursor.getDouble(cursor.getColumnIndex(DBHelper.FUEL_COL)+0))).toString()
        textTime.text = cursor.getString(cursor.getColumnIndex(DBHelper.TIME_COL)+0) + " " + getString(R.string.hours)
        textTotal.text = cursor.getString(cursor.getColumnIndex(DBHelper.PROFIT_COL)+0)
        textPerHour.text = ShiftHelper.calcAverageEarningsPerHour(shifts.last()).toString()
        val settings = this.activity?.getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val goalMonthString = settings?.getString("Goal_per_month", "")
        if (goalMonthString == null || goalMonthString == "")
        {
            textGoal.text = getString(R.string.n_a)
        }
        else
        {
            textGoal.text = ShiftHelper.calculateMonthProgress(shifts.last().date, db).toString() +
                    " " + getString(R.string.of) + " " + goalMonthString
        }
        cursor.close()
    }

    private fun setEmptyView ()
    {
        textDate.text = getString(R.string.n_a)
        textEarnings.text = getString(R.string.n_a)
        textCosts.text = getString(R.string.n_a)
        textTime.text = getString(R.string.n_a)
        textTotal.text = getString(R.string.n_a)
        textPerHour.text = getString(R.string.n_a)
        textGoal.text = getString(R.string.n_a)
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