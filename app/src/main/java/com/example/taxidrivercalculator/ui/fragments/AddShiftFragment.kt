package com.example.taxidrivercalculator.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.taxidrivercalculator.helpers.DBHelper
import com.example.taxidrivercalculator.ui.activities.MainActivity
import com.example.taxidrivercalculator.R
import com.example.taxidrivercalculator.helpers.ShiftHelper
import com.example.taxidrivercalculator.databinding.FragmentAddShiftBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddShiftFragment : Fragment(R.layout.fragment_add_shift) {

    object CalcShift {
        var date: String =""
        var onlineTime: Long = 0
        var breakTime: Long = 0
        var totalTime: Long = 0
        var earnings: Double = 0.0
        var wash: Double = 0.0
        var fuelCost: Double = 0.0
        var mileage: Double = 0.0
        var profit: Double = 0.0
    }

    private lateinit var currentShift: CalcShift
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
        currentShift= CalcShift

        bindItems()
        loadGuess()

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
        editDate.setOnFocusChangeListener { view, b ->  if (editDate.isFocused) pickDate(editDate)}
        editStart.setOnFocusChangeListener { view, b ->  if (editStart.isFocused) pickTime(editStart)}
        editEnd.setOnFocusChangeListener { view, b ->  if (editEnd.isFocused) pickTime(editEnd)}
        editBreakStart.setOnFocusChangeListener { view, b ->  if (editBreakStart.isFocused) pickTime(editBreakStart)}
        editBreakEnd.setOnFocusChangeListener { view, b ->  if (editBreakEnd.isFocused) pickTime(editBreakEnd)}
        checkBreak.setOnClickListener { switchBrake() }
        buttonSubmit.setOnClickListener {calculateShift()}

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
    @SuppressLint("SimpleDateFormat")
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

    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("H:mm")
        return format.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimeToLong(date: String): Long {
        val df = SimpleDateFormat("H:mm")
        return df.parse(date)!!.time
    }
    private fun hoursToMs (hours: Int): Long
    {
        return (hours*60*60*1000).toLong()
    }

    /*fun currentTimeToLong(): Long {
        return System.currentTimeMillis()
    }*/
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
        CalcShift.onlineTime = convertTimeToLong(editEnd.text.toString()) - convertTimeToLong(editStart.text.toString())
        if (CalcShift.onlineTime <0)
        {
            CalcShift.onlineTime += hoursToMs(24)
        }
        if (checkBreak.isChecked)
        {
            if (editBreakStart.text.toString()=="")
            {
                showErrorMessage(getString(R.string.break_start_is_empty))
                return
            }
            if (editBreakEnd.text.toString()=="")
            {
                showErrorMessage(getString(R.string.break_end_is_empty))
                return
            }
            CalcShift.breakTime =convertTimeToLong(editBreakEnd.text.toString())-convertTimeToLong(editBreakStart.text.toString())
            if (CalcShift.breakTime <0)
            {
                CalcShift.breakTime += hoursToMs(24)
            }
            CalcShift.totalTime = CalcShift.onlineTime - CalcShift.breakTime
        }
        else
        {
            CalcShift.totalTime = CalcShift.onlineTime
        }
        /*if (currentShift.totalTime > hoursToMs(12))
        {
            showWarningMessage("Your shift was for ${msToHours(currentShift.totalTime)} hours?")
        }
        if (currentShift.totalTime < hoursToMs(4))
        {
            showWarningMessage("Your shift was for ${msToHours(currentShift.totalTime)} hours?")
        }
        if (currentShift.breakTime > hoursToMs(4))
        {
            showWarningMessage("Your brake was for ${msToHours(currentShift.breakTime)} hours?")
        }*/
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

        CalcShift.earnings =editEarnings.text.toString().toDouble()
        CalcShift.wash =editWash.text.toString().toDouble()
        CalcShift.fuelCost =editFuelCost.text.toString().toDouble()
        CalcShift.mileage =editMileage.text.toString().toDouble()
        CalcShift.profit = CalcShift.earnings - CalcShift.wash - CalcShift.fuelCost

        showSubmitMessage(
            getString(
                R.string.you_earn_in_hours,
                CalcShift.profit.toString(),
                ShiftHelper.msToHours(CalcShift.totalTime).toString()
            ))

    }

    private fun showErrorMessage(errorCode: String)
    {
        val alert = AlertDialog.Builder(activity)
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
        val alert = AlertDialog.Builder(activity)
        alert.setTitle(getString(R.string.submit))
        alert.setPositiveButton(R.string.ok) {dialog, id -> submit()}
        alert.setNegativeButton(R.string.cancel, null)
        alert.setMessage(warningCode)
        alert.show()
    }

    private fun submit()
    {
        CalcShift.date =editDate.text.toString()

        /*val toast = Toast(activity)
        toast.setText("Success!")*/
        val db = DBHelper(requireActivity(), null)
        db.addShift(
            CalcShift.date,
            ShiftHelper.msToHours(CalcShift.totalTime),
            CalcShift.earnings,
            CalcShift.wash,
            CalcShift.fuelCost,
            CalcShift.mileage,
            CalcShift.profit
        )
        Toast.makeText(activity, getString(R.string.shift_added_successfully),Toast.LENGTH_SHORT).show()
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