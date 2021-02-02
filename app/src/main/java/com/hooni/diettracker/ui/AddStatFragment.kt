package com.hooni.diettracker.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.hooni.diettracker.R
import com.hooni.diettracker.databinding.FragmentInputBinding
import com.hooni.diettracker.ui.pickerdialogs.DatePickerDialogFragment
import com.hooni.diettracker.ui.pickerdialogs.TimePickerDialogFragment
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddStatFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentInputBinding
    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var date: TextView
    private lateinit var time: TextView

    override fun onStart() {
        super.onStart()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        dialog!!.window?.setLayout(width, WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInputBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        date = binding.textViewInputDate
        time = binding.textViewInputTime

        val currentDate = Calendar.getInstance()
        val currentHour = currentDate.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentDate.get(Calendar.MINUTE)
        val currentDayOfTheMonth = currentDate.get(Calendar.DAY_OF_MONTH)
        val currentMonth = currentDate.get(Calendar.MONTH)
        val currentYear = currentDate.get(Calendar.YEAR)

        date.text = getString(R.string.formatted_date, currentDayOfTheMonth, currentMonth, currentYear)
        time.text = getString(R.string.formatted_time, currentHour, currentMinute)

        date.setOnClickListener {
            DatePickerDialogFragment(this, currentDayOfTheMonth, currentMonth, currentYear).show(
                requireActivity().supportFragmentManager,
                "datePicker"
            )
        }
        time.setOnClickListener {
            TimePickerDialogFragment(
                this, currentHour, currentMinute
            ).show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        time.text = getString(R.string.formatted_time, hourOfDay, minute)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        date.text = getString(R.string.formatted_date, dayOfMonth, month, year)
    }
}