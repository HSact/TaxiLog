package com.example.taxidrivercalculator.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.taxidrivercalculator.helpers.DBHelper
import com.example.taxidrivercalculator.helpers.Shift
import com.example.taxidrivercalculator.helpers.ShiftHelper
import com.example.taxidrivercalculator.databinding.FragmentStatsBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null

    private val binding get() = _binding!!
    private lateinit var tableLayout: TableLayout
    private lateinit var textListIsEmtpy: TextView
    private lateinit var butDatePickBegin: EditText
    private lateinit var butDatePickEnd: EditText
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
    private var shiftsOrigin = mutableListOf<Shift>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        shiftsOrigin= ShiftHelper.makeArray(DBHelper(requireContext(), null))
        shifts = shiftsOrigin.toMutableList()
        bindItems()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val now = LocalDateTime.now()
        val currentDate = now.toLocalDate()
        val firstDayOfMonth = now.toLocalDate().withDayOfMonth(1)
        butDatePickBegin.setText(firstDayOfMonth.format(formatter))
        butDatePickEnd.setText(currentDate.format(formatter))
        updateShifts()
        butDatePickBegin.setOnClickListener {pickDate(butDatePickBegin)}
        butDatePickEnd.setOnClickListener {pickDate(butDatePickEnd)}

        displayInfo()
        return root
    }

    private fun pickDate(editObj: EditText) {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.selectedDate = editObj.text.toString()
        val supportFragmentManager = requireActivity().supportFragmentManager
        if (editObj == butDatePickBegin)
        {
            datePickerFragment.maxDate = butDatePickEnd.text.toString()
        }
        if (editObj == butDatePickEnd)
        {
            datePickerFragment.minDate = butDatePickBegin.text.toString()
        }
        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            this) { resultKey, bundle ->
            if (resultKey == "REQUEST_KEY") {
                val date = bundle.getString("SELECTED_DATE")
                editObj.setText(date.toString())
                updateShifts()
            }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
        updateShifts()
    }

    private fun updateShifts()
    {
        shifts = ShiftHelper.filterShiftsByDatePeriod(shiftsOrigin.toMutableList(), butDatePickBegin.text.toString(),
            butDatePickEnd.text.toString()).toMutableList()
        displayInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindItems ()
    {
        tableLayout = binding.tableLayout
        butDatePickBegin = binding.buttonDatePickBegin
        butDatePickEnd = binding.buttonDatePickEnd
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
        if (shifts.isEmpty())
        {
            textListIsEmtpy.visibility = View.VISIBLE
            tableLayout.visibility = View.GONE
            return
        }
        textListIsEmtpy.visibility = View.GONE
        tableLayout.visibility = View.VISIBLE
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