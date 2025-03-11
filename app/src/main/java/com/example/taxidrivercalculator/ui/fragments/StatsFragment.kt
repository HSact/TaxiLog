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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.fragment.app.viewModels

class StatsFragment : Fragment() {

    private val viewModel: StatsViewModel by viewModels()

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var tableLayout: TableLayout
    private lateinit var textListIsEmpty: TextView
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindItems()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        if (viewModel.startDate == null || viewModel.endDate == null) {
            val now = LocalDateTime.now()
            val currentDate = now.toLocalDate()
            val firstDayOfMonth = now.toLocalDate().withDayOfMonth(1)
            viewModel.startDate = firstDayOfMonth.format(formatter)
            viewModel.endDate = currentDate.format(formatter)
        }
        if (viewModel.shiftsOrigin.isEmpty() || viewModel.shifts.isEmpty()) {
            viewModel.shiftsOrigin = ShiftHelper.makeArray(DBHelper(requireContext(), null))
            viewModel.shifts = viewModel.shiftsOrigin.toMutableList()
        }
        butDatePickBegin.setText(viewModel.startDate)
        butDatePickEnd.setText(viewModel.endDate)
        viewModel.updateShifts()
        displayInfo()
        butDatePickBegin.setOnClickListener {pickDate(butDatePickBegin)}
        butDatePickEnd.setOnClickListener {pickDate(butDatePickEnd)}
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
                if (editObj == butDatePickBegin) {
                    viewModel.startDate = date
                } else if (editObj == butDatePickEnd) {
                    viewModel.endDate = date
                }
                viewModel.updateShifts()
                displayInfo()
            }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @SuppressLint("SetTextI18n")
    private fun displayInfo ()
    {
        shifts = viewModel.shifts
        if (shifts.isEmpty())
        {
            tableLayout.visibility = View.GONE
            textListIsEmpty.visibility = View.VISIBLE
            return
        }
        textListIsEmpty.visibility = View.GONE
        tableLayout.visibility = View.VISIBLE
        textShifts_count.text = viewModel.shiftsCount
        textAv_er_ph.text = viewModel.avErPh
        textAv_profit_ph.text  = viewModel.avProfitPh
        textAv_duration.text  = viewModel.avDuration
        textAv_mileage.text  = viewModel.avMileage
        textTotal_duration.text  = viewModel.totalDuration
        textTotal_mileage.text  = viewModel.totalMileage
        textTotal_wash.text  = viewModel.totalWash
        textTotalEarnings.text  = viewModel.totalEarnings
        textTotalProfit.text  = viewModel.totalProfit
        textAv_fuel.text  = viewModel.avFuel
        textTotal_fuel.text  = viewModel.totalFuel
    }
    private fun bindItems ()
    {
        tableLayout = binding.tableLayout
        butDatePickBegin = binding.buttonDatePickBegin
        butDatePickEnd = binding.buttonDatePickEnd
        textListIsEmpty = binding.textListIsEmpty
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
}