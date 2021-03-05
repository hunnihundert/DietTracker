package com.hooni.diettracker.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.hooni.diettracker.R
import com.hooni.diettracker.databinding.FragmentInputBinding
import com.hooni.diettracker.ui.pickerdialogs.DatePickerDialogFragment
import com.hooni.diettracker.ui.pickerdialogs.TimePickerDialogFragment
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import com.hooni.diettracker.util.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddStatFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentInputBinding
    private val mainViewModel: MainViewModel by sharedViewModel()

    private lateinit var title: TextView
    private lateinit var date: TextView
    private lateinit var time: TextView

    private lateinit var weightTextInputLayout: TextInputLayout
    private lateinit var waistTextInputLayout: TextInputLayout
    private lateinit var kCalTextInputLayout: TextInputLayout

    private lateinit var cancel: Button
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
        initObserver()
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.clearStats()
    }

    private fun initUi() {
        Log.d(TAG, "initUi: ${mainViewModel.getDateAndTime()}")
        title = binding.textViewInputTitle
        date = binding.textViewInputDate
        time = binding.textViewInputTime
        confirm = binding.buttonInputConfirm
        cancel = binding.buttonInputCancel

        weightTextInputLayout = binding.textInputLayoutInputWeight
        waistTextInputLayout = binding.textInputLayoutInputWaist
        kCalTextInputLayout = binding.textInputLayoutInputKcal

        if (tag == ADD_STAT_FRAGMENT_ADDING) {
            title.text = getString(R.string.textView_input_titleAddStat)
        } else {
            title.text = getString(R.string.textView_input_titleEditStat)
        }

        val currentDateAndTime = mainViewModel.getDateAndTime()

        Log.d(TAG, "initUi: $currentDateAndTime")
        Log.d(TAG, "initUi: ${mainViewModel.date.value}")
        Log.d(TAG, "initUi: ${mainViewModel.time.value}")

        date.setOnClickListener {
            DatePickerDialogFragment(
                this,
                currentDateAndTime.day,
                currentDateAndTime.month,
                currentDateAndTime.year,
                ADD_STAT_DATE_PICKER
            ).show(
                requireActivity().supportFragmentManager,
                DATE_PICKER
            )
        }
        time.setOnClickListener {
            TimePickerDialogFragment(
                this, currentDateAndTime.hour, currentDateAndTime.minute
            ).show(requireActivity().supportFragmentManager, TIME_PICKER)
        }

        confirm.setOnClickListener {
            clearTextInputError()
            mainViewModel.insertStat()
        }

        cancel.setOnClickListener {
            //mainViewModel.clearStats()
            dismiss()
        }
    }

    private fun clearTextInputError() {
        weightTextInputLayout.error = ""
        waistTextInputLayout.error = ""
        kCalTextInputLayout.error = ""
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        mainViewModel.setTime(hourOfDay, minute)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        mainViewModel.setDate(dayOfMonth, month+1, year)
    }

    private fun initObserver() {
        mainViewModel.insertStatStatus.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.ERROR -> {
                        resource.message?.let { message ->
                            view?.let { dialogView ->
                                Snackbar.make(
                                    dialogView,
                                    message,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                        showErrorOnEmptyTextInputField()
                    }
                    Status.LOADING -> {
                    }
                    Status.SUCCESS -> {
                        dialog!!.dismiss()
                    }
                }
            }
        })
    }


    private fun showErrorOnEmptyTextInputField() {
        if (weightTextInputLayout.editText?.text.isNullOrBlank()) {
            weightTextInputLayout.error = getString(R.string.textInputLayoutError_input_emptyField)
        }
        if (waistTextInputLayout.editText?.text.isNullOrBlank()) {
            waistTextInputLayout.error = getString(R.string.textInputLayoutError_input_emptyField)
        }
        if (kCalTextInputLayout.editText?.text.isNullOrBlank()) {
            kCalTextInputLayout.error = getString(R.string.textInputLayoutError_input_emptyField)
        }
    }

    companion object {
        private const val TAG = "AddStatFragment"
    }
}