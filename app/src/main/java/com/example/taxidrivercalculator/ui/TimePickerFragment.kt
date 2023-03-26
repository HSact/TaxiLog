package com.example.taxidrivercalculator.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private val clock = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // default date
        val hours = clock.get(Calendar.HOUR_OF_DAY)
        val minutes = clock.get(Calendar.MINUTE)
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            clock.set(Calendar.HOUR_OF_DAY, hour)
            clock.set(Calendar.MINUTE, minute)

        }
        //TimePickerDialog(requireActivity(), timeSetListener, clock.get(Calendar.HOUR_OF_DAY), clock.get(Calendar.MINUTE), true).show()

        // return new DatePickerDialog instance
        return TimePickerDialog(requireActivity(), this, clock.get(Calendar.HOUR_OF_DAY), clock.get(Calendar.MINUTE), true)
    }

    override fun onTimeSet(view: TimePicker?, hours: Int, minutes: Int) {
        clock.set(Calendar.HOUR_OF_DAY, hours)
        clock.set(Calendar.MINUTE, minutes)


        val selectedTime = SimpleDateFormat("H:mm", Locale.getDefault()).format(clock.time)

        val selectedTimeBundle = Bundle()
        selectedTimeBundle.putString("SELECTED_TIME", selectedTime)

        setFragmentResult("REQUEST_KEY", selectedTimeBundle)
    }


}