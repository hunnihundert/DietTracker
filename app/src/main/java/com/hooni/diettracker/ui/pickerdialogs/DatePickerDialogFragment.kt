package com.hooni.diettracker.ui.pickerdialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DatePickerDialogFragment(
    private val datePickerListener: DatePickerDialog.OnDateSetListener,
    private val day: Int,
    private val month: Int,
    private val year: Int
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(requireContext(), datePickerListener, year, month, day)
    }
}