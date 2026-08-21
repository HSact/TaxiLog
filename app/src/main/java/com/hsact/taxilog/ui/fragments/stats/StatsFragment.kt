package com.hsact.taxilog.ui.fragments.stats

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hsact.taxilog.R
import com.hsact.taxilog.databinding.FragmentStatsBinding
import com.hsact.taxilog.ui.AppTheme
import com.hsact.taxilog.ui.components.DatePickerFragment
import com.hsact.taxilog.ui.components.EmptyStateView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class StatsFragment : Fragment() {
    private val viewModel: StatsViewModel by viewModels()

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var countLayout: LinearLayout
    private lateinit var averageCard: CardView
    private lateinit var totalCard: CardView
    private lateinit var butDatePickBegin: EditText
    private lateinit var butDatePickEnd: EditText
    private lateinit var textShiftsCount: TextView
    private lateinit var textAvErPh: TextView
    private lateinit var textAvProfitPh: TextView
    private lateinit var textAvProfit: TextView
    private lateinit var textAvDuration: TextView
    private lateinit var textAvMileage: TextView
    private lateinit var textTotalDuration: TextView
    private lateinit var textTotalMileage: TextView
    private lateinit var textTotalWash: TextView
    private lateinit var textTotalService: TextView
    private lateinit var textTotalEarnings: TextView
    private lateinit var textTotalProfit: TextView
    private lateinit var textTotalTips: TextView
    private lateinit var textTotalTax: TextView
    private lateinit var textTotalExpenses: TextView
    private lateinit var textAvFuel: TextView
    private lateinit var textAvWash: TextView
    private lateinit var textTotalFuel: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        bindItems()

        binding.emptyStateCompose.setContent {
            AppTheme {
                EmptyStateView(
                    icon = Icons.Default.Info,
                    title = getString(R.string.stats_empty_title),
                    description = getString(R.string.stats_empty_description),
                )
            }
        }

        lifecycleScope.launch {
            viewModel.defineDates()
            viewModel.updateShifts(Locale.getDefault())
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiState.collect { uiState ->

                        if (!butDatePickBegin.isFocused &&
                            butDatePickBegin.text.toString() != uiState.startDate
                        ) {
                            butDatePickBegin.setText(uiState.startDate)
                        }

                        if (!butDatePickEnd.isFocused &&
                            butDatePickEnd.text.toString() != uiState.endDate
                        ) {
                            butDatePickEnd.setText(uiState.endDate)
                        }

                        displayInfo(uiState)
                    }
                }
            }
        }
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
                    viewModel.onBeginDateChange(date)
                } else if (editObj == butDatePickEnd) {
                    viewModel.onEndDateChange(date)
                }
                viewModel.updateShifts(Locale.getDefault())
            },
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun displayInfo(uiState: UiState) {
        val shifts = viewModel.shifts.value
        val isEmpty = shifts.isEmpty()

        countLayout.isVisible = !isEmpty
        averageCard.isVisible = !isEmpty
        totalCard.isVisible = !isEmpty
        binding.emptyStateCompose.isVisible = isEmpty

        if (isEmpty) return

        textShiftsCount.text = uiState.shiftsCount
        textAvErPh.text = uiState.avErPh
        textAvProfitPh.text = uiState.avProfitPh
        textAvProfit.text = uiState.avProfit
        textAvDuration.text = uiState.avDuration
        textAvMileage.text = uiState.avMileage
        textTotalDuration.text = uiState.totalDuration
        textTotalMileage.text = uiState.totalMileage
        textTotalWash.text = uiState.totalWash
        textTotalService.text = uiState.totalService
        textTotalEarnings.text = uiState.totalEarnings
        textTotalProfit.text = uiState.totalProfit
        textTotalTips.text = uiState.totalTips
        textTotalTax.text = uiState.totalTax
        textTotalExpenses.text = uiState.totalExpenses
        textAvFuel.text = uiState.avFuel
        textTotalFuel.text = uiState.totalFuel
        textAvWash.text = uiState.avWash
    }

    private fun bindItems() {
        countLayout = binding.layoutCount
        averageCard = binding.averageCard
        totalCard = binding.totalCard
        butDatePickBegin = binding.buttonDatePickBegin
        butDatePickEnd = binding.buttonDatePickEnd
        textShiftsCount = binding.textShiftsCountVal
        textAvErPh = binding.textAvErPhVal
        textAvProfitPh = binding.textAvProfitPhVal
        textAvProfit = binding.textAvProfitVal
        textAvDuration = binding.textAvDurationVal
        textAvMileage = binding.textAvMileageVal
        textTotalDuration = binding.textTotalDurationVal
        textTotalMileage = binding.textTotalMileageVal
        textTotalWash = binding.textTotalWashVal
        textTotalService = binding.textTotalServiceVal
        textTotalEarnings = binding.textTotalEarningsVal
        textTotalProfit = binding.textTotalProfitVal
        textTotalTips = binding.textTotalTipsVal
        textTotalTax = binding.textTotalTaxVal
        textTotalExpenses = binding.textTotalExpensesVal
        textAvFuel = binding.textAvFuelVal
        textAvWash = binding.textAvWashVal
        textTotalFuel = binding.textTotalFuelVal
    }
}
