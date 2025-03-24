package com.example.taxidrivercalculator.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private val calendar = Calendar.getInstance()
    lateinit var selectedDate: String
    var minDate = ""
    var maxDate = ""

    @SuppressLint("SimpleDateFormat")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var minDateLong: Long = 0
        var maxDateLong: Long = Long.MAX_VALUE
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        if (minDate.isNotEmpty()) {
            minDateLong = sdf.parse(minDate)?.time ?: 0
        }
        if (maxDate.isNotEmpty()) {
            maxDateLong = sdf.parse(maxDate)?.time ?: Long.MAX_VALUE
        }
        try {
            val date = sdf.parse(selectedDate)
            if (date != null) {
                calendar.time = date
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireActivity(), this, year, month, day)
        datePickerDialog.datePicker.minDate = minDateLong
        datePickerDialog.datePicker.maxDate = maxDateLong
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val selectedDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(calendar.time)

        val selectedDateBundle = Bundle()
        selectedDateBundle.putString("SELECTED_DATE", selectedDate)

        setFragmentResult("REQUEST_KEY", selectedDateBundle)
    }

    companion object {
        fun pickDate(
            context: Context,
            editObj: EditText,
            minDate: String = "",
            maxDate: String = "",
            onDatePicked: (() -> Unit)? = null
        ) {
            val datePickerFragment = DatePickerFragment().apply {
                this.minDate = minDate
                this.maxDate = maxDate
                this.selectedDate = editObj.text.toString()
            }

            val fragmentManager = when (context) {
                is AppCompatActivity -> context.supportFragmentManager
                is Fragment -> context.parentFragmentManager
                else -> throw IllegalArgumentException("Context must be an Activity or Fragment")
            }

            fragmentManager.setFragmentResultListener(
                "REQUEST_KEY", context as LifecycleOwner
            ) { resultKey, bundle ->
                if (resultKey == "REQUEST_KEY") {
                    editObj.setText(bundle.getString("SELECTED_DATE"))
                    onDatePicked?.invoke()
                }
            }
            datePickerFragment.show(fragmentManager, "DatePickerFragment")
        }
    }

}
