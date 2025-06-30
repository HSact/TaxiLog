package com.hsact.taxilog.ui.fragments.shiftForm

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hsact.taxilog.R
import com.hsact.taxilog.ui.activities.MainActivity
import com.hsact.taxilog.ui.components.DatePickerFragment
import com.hsact.taxilog.ui.components.TimePickerFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputLayout
import com.hsact.taxilog.databinding.FragmentShiftFormBinding
import com.hsact.taxilog.ui.shift.mappers.millisToHours
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class ShiftFormFragment : Fragment(R.layout.fragment_shift_form) {

    private val viewModel: ShiftFormViewModel by viewModels()

    private lateinit var editDate: EditText
    private lateinit var editStart: EditText
    private lateinit var editEnd: EditText
    private lateinit var switchBreak: MaterialSwitch
    private lateinit var editBreakStart: EditText
    private lateinit var editBreakEnd: EditText
    private lateinit var editEarnings: EditText
    private lateinit var editWash: EditText
    private lateinit var editFuelCost: EditText
    private lateinit var editMileage: EditText
    private lateinit var buttonSubmit: Button

    private lateinit var editDateL: TextInputLayout
    private lateinit var editStartL: TextInputLayout
    private lateinit var editEndL: TextInputLayout
    private lateinit var editBreakStartL: TextInputLayout
    private lateinit var editBreakEndL: TextInputLayout
    private lateinit var editEarningsL: TextInputLayout
    private lateinit var editWashL: TextInputLayout
    private lateinit var editFuelCostL: TextInputLayout
    private lateinit var editMileageL: TextInputLayout

    private lateinit var mileageWatcher: TextWatcher
    private var isProgrammaticChange = false

    private var _binding: FragmentShiftFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.botNav.isVisible = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentShiftFormBinding.inflate(inflater, container, false)
        bindItems()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mileageWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isProgrammaticChange) return
                val mileageValue = s?.toString()?.toDoubleOrNull()
                if (mileageValue != null) {
                    updateShiftField { it.mileage = mileageValue }
                    viewModel.guessFuelCost()
                } else {
                    editFuelCost.setText("")
                }
            }
        }
        editMileage.addTextChangedListener(mileageWatcher)
        val shiftId = arguments?.getInt("shiftId") ?: -1
        val visibleId = arguments?.getInt("visibleId") ?: -1
        if (shiftId != -1) {
            viewModel.loadShift(shiftId)
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.title =
                getString(R.string.title_edit_shift, visibleId)
        } else {
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.title =
                getString(R.string.title_new_shift)
        }
        viewModel.uiState.observe(viewLifecycleOwner)
        { shift -> updateUI(shift) }

        editDate.setOnClickListener {
            DatePickerFragment.pickDate(
                this,
                editDate,
                onDatePicked = { viewModel.uiState.value?.date = editDate.text.toString() }
            )
        }
        editStart.setOnClickListener {
            TimePickerFragment.pickTime(
                this, editStart,
                onTimePicked = { viewModel.uiState.value?.timeBegin = editStart.text.toString() }
            )
        }
        editEnd.setOnClickListener {
            TimePickerFragment.pickTime(
                this, editEnd,
                onTimePicked = { viewModel.uiState.value?.timeEnd = editEnd.text.toString() }
            )
        }
        editBreakStart.setOnClickListener {
            TimePickerFragment.pickTime(
                this, editBreakStart,
                onTimePicked = {
                    viewModel.uiState.value?.breakBegin = editBreakStart.text.toString()
                }
            )
        }
        editBreakEnd.setOnClickListener {
            TimePickerFragment.pickTime(
                this, editBreakEnd,
                onTimePicked = {
                    viewModel.uiState.value?.breakEnd = editBreakEnd.text.toString()
                }
            )
        }
        switchBreak.setOnClickListener { switchBrake() }
        buttonSubmit.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                calculateShift()
            }
        }
    }

    private fun updateShiftField(fieldSetter: (UiState) -> Unit) {
        val currentShift = viewModel.uiState.value ?: return
        fieldSetter(currentShift)
        viewModel.updateShift(currentShift)
    }

    private fun updateUI(shift: UiState) {
        isProgrammaticChange = true
        editDate.setText(shift.date)
        editStart.setText(shift.timeBegin)
        editEnd.setText(shift.timeEnd)
        editBreakStart.setText(shift.breakBegin)
        editBreakEnd.setText(shift.breakEnd)
        loadFinanceInput(shift)
        if (viewModel.uiState.value?.fuelCost != 0.0) {
            editFuelCost.setText(shift.fuelCost.toString())
        }
        if (editBreakStart.text.isNotEmpty() || editBreakEnd.text.isNotEmpty()) {
            switchBreak.isChecked = true
            binding.tableBreak.isVisible = true
        } else {
            switchBreak.isChecked = false
            binding.tableBreak.isVisible = false
        }
        isProgrammaticChange = false
    }

    private fun loadFinanceInput(uiState: UiState) {
        if (uiState.earnings != 0.0) {
            editEarnings.setText(uiState.earnings.toString())
        }
        if (uiState.wash != 0.0) {
            editWash.setText(uiState.wash.toString())
        }
        if (uiState.fuelCost != 0.0) {
            editFuelCost.setText(uiState.fuelCost.toString())
        }
        if (uiState.mileage != 0.0) {
            editMileage.setText(uiState.mileage.toString())
        }
    }

    private fun bindItems() {
        editDate = binding.buttonDatePick
        editStart = binding.buttonTimeStart
        editEnd = binding.buttonTimeEnd
        switchBreak = binding.switchBrake
        editBreakStart = binding.buttonTimeStartBrake
        editBreakEnd = binding.buttonTimeEndBrake
        editEarnings = binding.editTextEarnings
        editWash = binding.editTextWash
        editFuelCost = binding.editTextFuelCost
        editMileage = binding.editTextMileage
        buttonSubmit = binding.buttonSubmit

        editDateL = binding.buttonDatePickL
        editStartL = binding.buttonTimeStartL
        editEndL = binding.buttonTimeEndL
        editBreakStartL = binding.buttonTimeStartL
        editBreakEndL = binding.buttonTimeEndL
        editEarningsL = binding.editTextEarningsL
        editWashL = binding.editTextWashL
        editFuelCostL = binding.editTextFuelCostL
        editMileageL = binding.editTextMileageL
    }

    private fun calculateShift() {
        if (editEarnings.text.isEmpty() || editFuelCost.text.isEmpty() || editMileage.text.isEmpty()) {
            if (editFuelCost.text.isEmpty()) {
                editFuelCost.setError(getString(R.string.fuel_cost_is_empty), null)

                editFuelCost.requestFocus()
            }
            if (editMileage.text.isEmpty()) {
                editMileage.setError(getString(R.string.mileage_is_empty), null)
                editMileage.requestFocus()
            }
            if (editEarnings.text.isEmpty()) {
                editEarnings.setError(getString(R.string.earnings_is_empty), null)
                editEarnings.requestFocus()
            }
            return
        }

        viewModel.calculateShift(
            editEarnings.text.toString().toDoubleOrNull() ?: 0.0,
            editWash.text.toString().toDoubleOrNull() ?: 0.0,
            editFuelCost.text.toString().toDoubleOrNull() ?: 0.0,
            editMileage.text.toString().toDoubleOrNull() ?: 0.0
        )

        showSubmitMessage(
            getString(
                R.string.you_earn_in_hours,
                viewModel.uiState.value?.profit.toString(),
                (viewModel.uiState.value?.totalTime ?: 0).millisToHours(Locale.getDefault())
            )
        )
    }

    private fun showSubmitMessage(warningCode: String) {
        val alert = MaterialAlertDialogBuilder(requireContext())
        alert.setTitle(getString(R.string.submit))
        alert.setPositiveButton(R.string.ok) { dialog, id ->
            viewLifecycleOwner.lifecycleScope.launch {
                submit()
            }
        }
        alert.setNegativeButton(R.string.cancel, null)
        alert.setMessage(warningCode)
        alert.show()
    }

    private suspend fun submit() {
        Toast.makeText(activity, getString(R.string.shift_added_successfully), Toast.LENGTH_SHORT)
            .show()
        viewModel.submit()
        findNavController().navigate(R.id.action_shiftForm_to_home_fragment)
        MainActivity.botNav.isVisible = true
    }

    override fun onResume() {
        MainActivity.botNav.isVisible = false
        super.onResume()
    }

    override fun onDestroy() {
        MainActivity.botNav.isVisible = true
        super.onDestroy()
    }

    private fun switchBrake() {
        animateHeightChange(binding.tableBreak)
    }

    private fun animateHeightChange(view: View) {
        val parentLayout = view.parent as ViewGroup
        val visibility = if (view.isVisible) View.GONE else View.VISIBLE
        TransitionManager.beginDelayedTransition(parentLayout)
        view.visibility = visibility
    }
}