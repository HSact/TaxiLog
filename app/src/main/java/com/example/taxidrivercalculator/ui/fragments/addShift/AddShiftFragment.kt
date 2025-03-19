package com.example.taxidrivercalculator.ui.fragments.addShift

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
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

class AddShiftFragment : Fragment(R.layout.fragment_add_shift) {

    private val viewModel = AddShiftViewModel()

    private lateinit var editDate: EditText
    private lateinit var editStart: EditText
    private lateinit var editEnd: EditText
    private lateinit var checkBreak: CheckBox
    private lateinit var editBreakStart: EditText
    private lateinit var editBreakEnd: EditText
    private lateinit var editEarnings: EditText
    private lateinit var editWash: EditText
    private lateinit var editFuelCost: EditText
    private lateinit var editMileage: EditText
    private lateinit var buttonSubmit: Button

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

        editDate.setOnClickListener { pickDate(editDate) }
        editStart.setOnClickListener { pickTime(editStart) }
        editEnd.setOnClickListener { pickTime(editEnd) }
        editBreakStart.setOnClickListener { pickTime(editBreakStart) }
        editBreakEnd.setOnClickListener { pickTime(editBreakEnd) }
        checkBreak.setOnClickListener { switchBrake() }
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

    private fun updateShiftField(fieldSetter: (CalcShift) -> Unit) {
        val currentShift = viewModel.shiftData.value ?: return
        fieldSetter(currentShift)
        viewModel.updateShift(currentShift)
    }

    private fun updateUI(shift: CalcShift) {
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
            checkBreak.isChecked = true
            binding.tableBreak.isVisible = true
        }
        else
        {
            checkBreak.isChecked = false
            binding.tableBreak.isVisible = false
        }
    }

    private fun bindItems ()
    {
        editDate = binding.buttonDatePick
        editStart = binding.buttonTimeStart
        editEnd = binding.buttonTimeEnd
        checkBreak = binding.checkBreak
        editBreakStart = binding.buttonTimeStartBrake
        editBreakEnd = binding.buttonTimeEndBrake
        editEarnings = binding.editTextEarnings
        editWash = binding.editTextWash
        editFuelCost = binding.editTextFuelCost
        editMileage = binding.editTextMileage
        buttonSubmit = binding.buttonSubmit
    }

    private fun pickDate(editObj: EditText)
    {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.selectedDate = editObj.text.toString()
        val supportFragmentManager = requireActivity().supportFragmentManager
        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
                viewLifecycleOwner)
        { resultKey, bundle ->
            if (resultKey == "REQUEST_KEY") {
                val date = bundle.getString("SELECTED_DATE")
                editObj.setText(date.toString())
            }
        }
        datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
    }

    private fun pickTime(editObj: EditText)
    {
        // create new instance of DatePickerFragment
        val timePickerFragment = TimePickerFragment()
        val supportFragmentManager = requireActivity().supportFragmentManager

        // we have to implement setFragmentResultListener
        supportFragmentManager.setFragmentResultListener(
            "REQUEST_KEY",
            viewLifecycleOwner)
        { resultKey, bundle ->
            if (resultKey == "REQUEST_KEY") {
                val time = bundle.getString("SELECTED_TIME")
                editObj.setText(time.toString())
            }
        }
        timePickerFragment.show(supportFragmentManager, "TimePickerFragment")
    }

    private fun calculateShift ()
    {
        if (editEarnings.text.isEmpty() || editWash.text.isEmpty() ||
            editFuelCost.text.isEmpty() || editMileage.text.isEmpty())
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

    /*private fun showErrorMessage(errorCode: String)
    {
        val alert = MaterialAlertDialogBuilder(requireContext())
        alert.setTitle(getString(R.string.error))
        alert.setPositiveButton(R.string.ok, null)
        alert.setMessage(errorCode)
        alert.show()
    }*/

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
        val check1: CheckBox = binding.checkBreak
        val row = binding.tableBreak
        row.isVisible = check1.isChecked
    }
}