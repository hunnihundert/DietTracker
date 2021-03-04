package com.hooni.diettracker.ui.pickerdialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment

class DatePickerDialogFragment(
    private val datePickerListener: DatePickerDialog.OnDateSetListener,
    private val day: Int,
    private val month: Int,
    private val year: Int,
    private val pickerTag: String,
) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val picker = DatePickerDialog(requireContext(), datePickerListener, year, month-1, day)
        picker.datePicker.tag = pickerTag
        return picker
    }

    companion object {
        private const val TAG = "DatePickerDialogFragmen"
    }
}