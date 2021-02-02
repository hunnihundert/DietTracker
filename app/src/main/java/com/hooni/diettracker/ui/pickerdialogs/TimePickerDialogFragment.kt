package com.hooni.diettracker.ui.pickerdialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment

class TimePickerDialogFragment(
    private val timeSetListener: TimePickerDialog.OnTimeSetListener,
    private val currentHourString: Int,
    private val currentMinuteString: Int
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return TimePickerDialog(
            requireContext(),
            timeSetListener,
            currentHourString,
            currentMinuteString,
            DateFormat.is24HourFormat(requireContext())
        )
    }
}