package com.hsact.taxilog.ui.components

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import java.text.SimpleDateFormat
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private val clock = Calendar.getInstance()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val selectedTime = arguments?.getString("SELECTED_TIME") ?: ""
        try {
            val time = SimpleDateFormat("H:mm", Locale.getDefault()).parse(selectedTime)
            if (time != null) {
                clock.time = time
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val hours = clock.get(Calendar.HOUR_OF_DAY)
        val minutes = clock.get(Calendar.MINUTE)

        return TimePickerDialog(requireActivity(), this, hours, minutes, true)
    }

    override fun onTimeSet(view: TimePicker?, hours: Int, minutes: Int) {
        clock.set(Calendar.HOUR_OF_DAY, hours)
        clock.set(Calendar.MINUTE, minutes)
        val selectedTime = SimpleDateFormat("H:mm", Locale.getDefault()).format(clock.time)

        val selectedTimeBundle = Bundle().apply {
            putString("SELECTED_TIME", selectedTime)
        }
        setFragmentResult("REQUEST_KEY", selectedTimeBundle)
    }

    companion object {
        fun pickTime(fragment: Fragment,
            editObj: EditText,
            onTimePicked: (() -> Unit)? = null,
        ) {
            val timePickerFragment = TimePickerFragment().apply {
                arguments = Bundle().apply {
                    putString("SELECTED_TIME", editObj.text.toString())
                }
            }

            fragment.parentFragmentManager.setFragmentResultListener(
                "REQUEST_KEY", fragment.viewLifecycleOwner
            ) { resultKey, bundle ->
                if (resultKey == "REQUEST_KEY") {
                    editObj.setText(bundle.getString("SELECTED_TIME"))
                    onTimePicked?.invoke()
                }
            }
            timePickerFragment.show(fragment.parentFragmentManager, "TimePickerFragment")
        }
    }
}