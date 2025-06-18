package com.hsact.taxilog.ui.fragments.stats

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hsact.taxilog.databinding.FragmentStatsBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hsact.taxilog.ui.components.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatsFragment : Fragment() {

    private val viewModel: StatsViewModel by viewModels()

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var tableLayout: TableLayout
    private lateinit var textListIsEmpty: TextView
    private lateinit var butDatePickBegin: EditText
    private lateinit var butDatePickEnd: EditText
    private lateinit var textShiftsCount: TextView
    private lateinit var textAvErPh: TextView
    private lateinit var textAvProfitPh: TextView
    private lateinit var textAvDuration: TextView
    private lateinit var textAvMileage: TextView
    private lateinit var textTotalDuration: TextView
    private lateinit var textTotalMileage: TextView
    private lateinit var textTotalWash: TextView
    private lateinit var textTotalEarnings: TextView
    private lateinit var textTotalProfit: TextView
    private lateinit var textAvFuel: TextView
    private lateinit var textTotalFuel: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindItems()
        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shifts.collect {
                    displayInfo()
                }
            }
        }
        viewModel.defineDates()
        butDatePickBegin.setText(viewModel.startDate)
        butDatePickEnd.setText(viewModel.endDate)
        butDatePickBegin.setOnClickListener { pickDate(butDatePickBegin) }
        butDatePickEnd.setOnClickListener { pickDate(butDatePickEnd) }
        return root
    }

    private fun pickDate(editObj: EditText) {
        DatePickerFragment.pickDate(
            context = this,
            editObj = editObj,
            minDate = if (editObj == butDatePickEnd) butDatePickBegin.text.toString() else "",
            maxDate = if (editObj == butDatePickBegin) butDatePickEnd.text.toString() else "",
            onDatePicked = {
                val date = editObj.text.toString()
                if (editObj == butDatePickBegin) {
                    viewModel.startDate = date
                } else if (editObj == butDatePickEnd) {
                    viewModel.endDate = date
                }
                viewModel.updateShifts()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun displayInfo() {
        val shifts = viewModel.shiftsLegacy
        if (shifts.isEmpty()) {
            tableLayout.visibility = View.GONE
            textListIsEmpty.visibility = View.VISIBLE
            return
        }
        textListIsEmpty.visibility = View.GONE
        tableLayout.visibility = View.VISIBLE
        textShiftsCount.text = viewModel.shiftsCount
        textAvErPh.text = viewModel.avErPh
        textAvProfitPh.text = viewModel.avProfitPh
        textAvDuration.text = viewModel.avDuration
        textAvMileage.text = viewModel.avMileage
        textTotalDuration.text = viewModel.totalDuration
        textTotalMileage.text = viewModel.totalMileage
        textTotalWash.text = viewModel.totalWash
        textTotalEarnings.text = viewModel.totalEarnings
        textTotalProfit.text = viewModel.totalProfit
        textAvFuel.text = viewModel.avFuel
        textTotalFuel.text = viewModel.totalFuel
    }

    private fun bindItems() {
        tableLayout = binding.tableLayout
        butDatePickBegin = binding.buttonDatePickBegin
        butDatePickEnd = binding.buttonDatePickEnd
        textListIsEmpty = binding.textListIsEmpty
        textShiftsCount = binding.textShiftsCountVal
        textAvErPh = binding.textAvErPhVal
        textAvProfitPh = binding.textAvProfitPhVal
        textAvDuration = binding.textAvDurationVal
        textAvMileage = binding.textAvMileageVal
        textTotalDuration = binding.textTotalDurationVal
        textTotalMileage = binding.textTotalMileageVal
        textTotalWash = binding.textTotalWashVal
        textTotalEarnings = binding.textTotalEarningsVal
        textTotalProfit = binding.textTotalProfitVal
        textAvFuel = binding.textAvFuelVal
        textTotalFuel = binding.textTotalFuelVal
    }
}