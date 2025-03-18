package com.example.taxidrivercalculator.ui.fragments.addShift

import android.annotation.SuppressLint
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
import com.example.taxidrivercalculator.helpers.ShiftHelper.convertLongToTime
import com.example.taxidrivercalculator.helpers.ShiftHelper.convertTimeToLong
import com.example.taxidrivercalculator.ui.activities.MainActivity
import com.example.taxidrivercalculator.ui.fragments.DatePickerFragment
import com.example.taxidrivercalculator.ui.fragments.TimePickerFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddShiftFragment : Fragment(R.layout.fragment_add_shift) {

    /*object CalcShift {
        var date: String =""
        var timeBegin: String =""
        var timeEnd: String =""
        var breakBegin: String =""
        var breakEnd: String =""
        var onlineTime: Long = 0
        var breakTime: Long = 0
        var totalTime: Long = 0
        var earnings: Double = 0.0
        var wash: Double = 0.0
        var fuelCost: Double = 0.0
        var mileage: Double = 0.0
        var profit: Double = 0.0
    }*/

    private val viewModel = AddShiftViewModel()

    //private lateinit var currentShift: CalcShift
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
        //currentShift= CalcShift

        bindItems()
        //loadGuess()

        editMileage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?)
            {
                    guessFuelCost()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*editDate.setOnClickListener() {
            pickDate()
        }*/
        viewModel.shiftData.observe (viewLifecycleOwner)
        { shift -> updateUI(shift)
        }
        editDate.setOnClickListener { pickDate(editDate) }
        editStart.setOnClickListener { pickTime(editStart) }
        editEnd.setOnClickListener { pickTime(editEnd) }
        editBreakStart.setOnClickListener { pickTime(editBreakStart) }
        editBreakEnd.setOnClickListener { pickTime(editBreakEnd) }
        checkBreak.setOnClickListener { switchBrake() }
        buttonSubmit.setOnClickListener {calculateShift()}
    }

    private fun updateUI(shift: CalcShift) {
        editDate.setText(shift.date)
        editStart.setText(shift.timeBegin)
        editEnd.setText(shift.timeEnd)
        editBreakStart.setText(shift.breakBegin)
        editBreakEnd.setText(shift.breakEnd)
        editEarnings.setText(shift.earnings.toString())
        editWash.setText(shift.wash.toString())
        editFuelCost.setText(shift.fuelCost.toString())
        editMileage.setText(shift.mileage.toString())
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

    private fun loadGuess() {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

        val now = LocalDateTime.now()
        val endDate = now.toLocalDate()
        val endTime = now.toLocalTime().format(timeFormatter)

        val beginTime = convertLongToTime(convertTimeToLong(endTime) - hoursToMs(10))
        val beginDate = endDate.minusDays(1)

        editEnd.setText(endTime)
        editStart.setText(beginTime)

        if (convertTimeToLong(beginTime) > convertTimeToLong(endTime)) {
            editDate.setText(beginDate.format(formatter))
        }
        else
        {
            editDate.setText(endDate.format(formatter))
        }
    }

    private fun hoursToMs (hours: Int): Long
    {
        return (hours*60*60*1000).toLong()
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
        // show
        timePickerFragment.show(supportFragmentManager, "TimePickerFragment")
    }

    @SuppressLint("SetTextI18n")
    private fun guessFuelCost ()
    {
        val settings = requireContext().getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val fuelPrice: Double = settings.getString("Fuel_price", "0")?.toDoubleOrNull() ?: 0.0
        val consumption: Double = settings.getString("Consumption", "0")?.toDoubleOrNull() ?: 0.0
        if (!(settings.getBoolean("Seted_up", false)) || fuelPrice == 0.0 || consumption == 0.0)
        {
            return
        }
        if (editMileage.text.isEmpty())
        {
            editFuelCost.setText("")
            return
        }

        val n = ShiftHelper.centsRound(
            fuelPrice * editMileage.text.toString().toDouble() * consumption / 100
        )
        editFuelCost.setText(n.toString())
    }

    private fun calculateShift ()
    {

        if (editEarnings.text.isEmpty())
        {
            showErrorMessage(getString(R.string.earnings_is_empty))
            return
        }
        if (editWash.text.isEmpty())
        {
            editWash.setText("0")
        }
        if (editFuelCost.text.isEmpty())
        {
            showErrorMessage(getString(R.string.fuel_cost_is_empty))
            return
        }
        if (editMileage.text.isEmpty())
        {
            showErrorMessage(getString(R.string.mileage_is_empty))
            return
        }

        showSubmitMessage(
            getString(
                R.string.you_earn_in_hours,
                viewModel.shiftData.value?.profit.toString(),
                ShiftHelper.msToHours(viewModel.shiftData.value?.totalTime ?: 0).toString()
            ))
    }

    private fun showErrorMessage(errorCode: String)
    {
        val alert = MaterialAlertDialogBuilder(requireContext())
        alert.setTitle(getString(R.string.error))
        alert.setPositiveButton(R.string.ok, null)
        alert.setMessage(errorCode)
        alert.show()

    }
    /*private fun showWarningMessage (warningCode: String)
    {
        val alert = AlertDialog.Builder(activity)
        alert.setTitle("Warning")
        alert.setPositiveButton("OK", null)
        alert.setNegativeButton("CANCEL") {dialog, id -> onResume()}
        alert.setMessage(warningCode)
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
        /*CalcShift.date =editDate.text.toString()

        val db = DBHelper(requireActivity(), null)
        db.addShift(
            CalcShift.date,
            ShiftHelper.msToHours(CalcShift.totalTime),
            CalcShift.earnings,
            CalcShift.wash,
            CalcShift.fuelCost,
            CalcShift.mileage,
            CalcShift.profit
        )*/
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