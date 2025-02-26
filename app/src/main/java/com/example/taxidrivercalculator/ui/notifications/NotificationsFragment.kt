package com.example.taxidrivercalculator.ui.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taxidrivercalculator.DBHelper
import com.example.taxidrivercalculator.MainActivity
import com.example.taxidrivercalculator.Shift
import com.example.taxidrivercalculator.ShiftHelper
import com.example.taxidrivercalculator.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var tableLayout: TableLayout
    private lateinit var textListIsEmtpy: TextView
    private lateinit var textShifts_count: TextView
    private lateinit var textAv_er_ph: TextView
    private lateinit var textAv_profit_ph: TextView
    private lateinit var textAv_duration: TextView
    private lateinit var textAv_mileage: TextView
    private lateinit var textTotal_duration: TextView
    private lateinit var textTotal_mileage: TextView
    private lateinit var textTotal_wash: TextView
    private lateinit var textTotalEarnings: TextView
    private lateinit var textTotalProfit: TextView
    private lateinit var textAv_fuel: TextView
    private lateinit var textTotal_fuel: TextView

    private var shifts = mutableListOf<Shift>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        shifts=ShiftHelper.makeArray(DBHelper(requireContext(), null))
        bindItems()
        if (shifts.size!=0)
        {
            textListIsEmtpy.visibility = View.GONE
            tableLayout.visibility = View.VISIBLE
            displayInfo()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindItems ()
    {
        tableLayout = binding.tableLayout
        textListIsEmtpy = binding.textListIsEmpty
        textShifts_count = binding.textShiftsCountVal
        textAv_er_ph = binding.textAvErPhVal
        textAv_profit_ph = binding.textAvProfitPhVal
        textAv_duration = binding.textAvDurationVal
        textAv_mileage = binding.textAvMileageVal
        textTotal_duration = binding.textTotalDurationVal
        textTotal_mileage = binding.textTotalMileageVal
        textTotal_wash = binding.textTotalWashVal
        textTotalEarnings = binding.textTotalEarningsVal
        textTotalProfit = binding.textTotalProfitVal
        textAv_fuel = binding.textAvFuelVal
        textTotal_fuel = binding.textTotalFuelVal
    }
    @SuppressLint("SetTextI18n")
    private fun displayInfo ()
    {
        textShifts_count.text = shifts.size.toString()
        textAv_er_ph.text = ShiftHelper.calcAverageEarningsPerHour(shifts).toString()
        textAv_profit_ph.text  = ShiftHelper.calcAverageProfitPerHour(shifts).toString()
        textAv_duration.text  = ShiftHelper.calcAverageShiftDuration(shifts).toString()
        textAv_mileage.text  = ShiftHelper.calcAverageMileage(shifts).toString()
        textTotal_duration.text  = ShiftHelper.calcTotalShiftDuration(shifts).toString()
        textTotal_mileage.text  = ShiftHelper.calcTotalMileage(shifts).toString()
        textTotal_wash.text  = ShiftHelper.calcTotalWash(shifts).toString()
        textTotalEarnings.text  = ShiftHelper.calcTotalEarnings(shifts).toString()
        textTotalProfit.text  = ShiftHelper.calcTotalProfit(shifts).toString()
        textAv_fuel.text  = ShiftHelper.calcAverageFuelCost(shifts).toString()
        textTotal_fuel.text  = ShiftHelper.calcTotalFuelCost(shifts).toString()
    }
}