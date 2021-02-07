package com.hooni.diettracker.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.hooni.diettracker.databinding.FragmentInputBinding
import com.hooni.diettracker.ui.pickerdialogs.DatePickerDialogFragment
import com.hooni.diettracker.ui.pickerdialogs.TimePickerDialogFragment
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import com.hooni.diettracker.util.DateAndTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddStatFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentInputBinding
    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var date: TextView
    private lateinit var time: TextView

    private lateinit var confirm: Button

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
        binding.viewModel = mainViewModel
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
        confirm = binding.buttonInputConfirm

        val calendar = Calendar.getInstance()
        val currentDateAndTime = DateAndTime.fromCalendar(calendar)

        mainViewModel.setDateAndTime(currentDateAndTime)

        date.setOnClickListener {
            DatePickerDialogFragment(this, currentDateAndTime.day, currentDateAndTime.month, currentDateAndTime.year).show(
                requireActivity().supportFragmentManager,
                "datePicker"
            )
        }
        time.setOnClickListener {
            TimePickerDialogFragment(
                this, currentDateAndTime.hour, currentDateAndTime.minute
            ).show(requireActivity().supportFragmentManager, "timePicker")
        }

        confirm.setOnClickListener {
            mainViewModel.insertStat()
            dialog!!.hide()
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        mainViewModel.setTime(hourOfDay, minute)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        mainViewModel.setDate(dayOfMonth,month,year)
    }
}