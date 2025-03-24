package com.example.taxidrivercalculator.ui.fragments.addShift

import android.app.Activity
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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.databinding.FragmentAddShiftBinding
import com.example.taxidrivercalculator.helpers.ShiftHelper
import com.example.taxidrivercalculator.ui.activities.MainActivity
import com.example.taxidrivercalculator.ui.fragments.DatePickerFragment
import com.example.taxidrivercalculator.ui.fragments.TimePickerFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputLayout

class AddShiftFragment : Fragment(R.layout.fragment_add_shift) {

    private val viewModel = AddShiftViewModel()

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

    private var _binding: FragmentAddShiftBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.botNav.isVisible = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddShiftBinding.inflate(inflater, container, false)
        bindItems()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.shiftData.observe (viewLifecycleOwner)
        { shift -> updateUI(shift) }
        //editDateL.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.teal_200)
        //MaterialColors.getColor(editDateL, com.google.android.material.R.attr.colorPrimary)
        /*if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("IS_VISIBLE_TABLE_BREAK", true)) {
                binding.tableBreak.visibility = View.VISIBLE
            } else {
                binding.tableBreak.visibility = View.GONE
            }
        }*/
        //putDateAndTimeToVM()

        editDate.setOnClickListener { pickDate(editDate) }
        editStart.setOnClickListener { TimePickerFragment.pickTime(this, editStart)
            putDateAndTimeToVM() }
        editEnd.setOnClickListener { TimePickerFragment.pickTime(this, editEnd)
            putDateAndTimeToVM() }
        editBreakStart.setOnClickListener { TimePickerFragment.pickTime(this, editBreakStart)
            putDateAndTimeToVM() }
        editBreakEnd.setOnClickListener { TimePickerFragment.pickTime(this, editBreakEnd)
            putDateAndTimeToVM() }
        switchBreak.setOnClickListener { switchBrake() }
        buttonSubmit.setOnClickListener {calculateShift()}

        editMileage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty())
                {
                    editFuelCost.setText("")
                    return
                }
                updateShiftField { it.mileage = s.toString().toDoubleOrNull() ?: 0.0 }
                viewModel.guessFuelCost(requireContext().getSharedPreferences("Settings", Activity.MODE_PRIVATE))
            }
        })
    }

    private fun updateShiftField(fieldSetter: (AddShiftData) -> Unit) {
        val currentShift = viewModel.shiftData.value ?: return
        fieldSetter(currentShift)
        viewModel.updateShift(currentShift)
    }

    private fun updateUI(shift: AddShiftData) {
        editDate.setText(shift.date)
        editStart.setText(shift.timeBegin)
        editEnd.setText(shift.timeEnd)
        editBreakStart.setText(shift.breakBegin)
        editBreakEnd.setText(shift.breakEnd)
        /*if (viewModel.shiftData.value?.earnings != 0.0)
        {
            editEarnings.setText(shift.earnings.toString())
        }
        if (viewModel.shiftData.value?.wash != 0.0)
        {
            editWash.setText(shift.wash.toString())
        }*/
        if (viewModel.shiftData.value?.fuelCost != 0.0)
        {
            editFuelCost.setText(shift.fuelCost.toString())
        }
        /*if (viewModel.shiftData.value?.mileage != 0.0)
        {
            editMileage.setText(shift.mileage.toString())
        }*/
        if (editBreakStart.text.isNotEmpty() || editBreakEnd.text.isNotEmpty())
        {
            switchBreak.isChecked = true
            binding.tableBreak.isVisible = true
        }
        else
        {
            switchBreak.isChecked = false
            binding.tableBreak.isVisible = false
        }
    }

    private fun bindItems ()
    {
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

    private fun pickDate(editObj: EditText)
    {
        /*val datePickerFragment = DatePickerFragment()
        datePickerFragment.selectedDate = editObj.text.toString()
        val supportFragmentManager = requireActivity().supportFragmentManager
        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
                viewLifecycleOwner)
        { resultKey, bundle ->
            if (resultKey == "REQUEST_KEY") {
                val date = bundle.getString("SELECTED_DATE")
                editObj.setText(date.toString())
                putDateAndTimeToVM()
            }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")*/
        DatePickerFragment.pickDate(requireContext(), editObj) {
            putDateAndTimeToVM()
        }
    }

    private fun calculateShift ()
    {
        if (editEarnings.text.isEmpty() || editFuelCost.text.isEmpty() || editMileage.text.isEmpty())
        {
            if (editFuelCost.text.isEmpty())
            {
                editFuelCost.setError(getString(R.string.fuel_cost_is_empty), null)

                editFuelCost.requestFocus()
            }
            if (editMileage.text.isEmpty())
            {
                editMileage.setError(getString(R.string.mileage_is_empty), null)
                editMileage.requestFocus()
            }
            if (editEarnings.text.isEmpty())
            {
                editEarnings.setError(getString(R.string.earnings_is_empty), null)
                editEarnings.requestFocus()
            }
            return
        }
        putDateAndTimeToVM()
        viewModel.shiftData.value?.earnings = editEarnings.text.toString().toDoubleOrNull() ?: 0.0
        viewModel.shiftData.value?.wash = editWash.text.toString().toDoubleOrNull() ?: 0.0
        viewModel.shiftData.value?.fuelCost = editFuelCost.text.toString().toDoubleOrNull() ?: 0.0
        viewModel.shiftData.value?.mileage = editMileage.text.toString().toDoubleOrNull() ?: 0.0

        viewModel.calculateShift()

        showSubmitMessage(
            getString(
                R.string.you_earn_in_hours,
                viewModel.shiftData.value?.profit.toString(),
                ShiftHelper.msToHours(viewModel.shiftData.value?.totalTime ?: 0).toString()
            ))
    }

    private fun putDateAndTimeToVM() {
        viewModel.shiftData.value?.date = editDate.text.toString()
        viewModel.shiftData.value?.timeBegin = editStart.text.toString()
        viewModel.shiftData.value?.timeEnd = editEnd.text.toString()
        viewModel.shiftData.value?.breakBegin = editBreakStart.text.toString()
        viewModel.shiftData.value?.breakEnd = editBreakEnd.text.toString()
    }

    private fun showSubmitMessage (warningCode: String)
    {
        val alert = MaterialAlertDialogBuilder(requireContext())
        alert.setTitle(getString(R.string.submit))
        alert.setPositiveButton(R.string.ok) {dialog, id -> submit()}
        alert.setNegativeButton(R.string.cancel, null)
        alert.setMessage(warningCode)
        alert.show()
    }

    private fun submit()
    {
        Toast.makeText(activity, getString(R.string.shift_added_successfully),Toast.LENGTH_SHORT).show()
        viewModel.submit(requireContext())
        findNavController().navigate(R.id.action_addShift_to_home_fragment)
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

    private fun switchBrake()
    {
        //row.isVisible = check1.isChecked
        animateHeightChange(binding.tableBreak)
    }

    private fun animateHeightChange(view: View) {
        val parentLayout = view.parent as ViewGroup
        val visibility = if (view.isVisible) View.GONE else View.VISIBLE
        TransitionManager.beginDelayedTransition(parentLayout)
        view.visibility = visibility
    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("IS_VISIBLE_TABLE_BREAK", binding.tableBreak.visibility == View.VISIBLE)
    }*/
}