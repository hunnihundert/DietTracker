package com.hooni.diettracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.hooni.diettracker.R
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.databinding.FragmentStatsBinding
import com.hooni.diettracker.ui.adapter.StatsAdapter
import com.hooni.diettracker.ui.pickerdialogs.DatePickerDialogFragment
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import com.hooni.diettracker.util.ADD_STAT_DATE_PICKER
import com.hooni.diettracker.util.DateAndTime
import com.hooni.diettracker.util.ENDING_DATE_PICKER
import com.hooni.diettracker.util.STARTING_DATE_PICKER
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class StatsFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentStatsBinding
    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var statsAdapter: StatsAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var startingDate: TextView
    private lateinit var endingDate: TextView

    private lateinit var addStats: FloatingActionButton

    private lateinit var addStatFragment: DialogFragment

    private val stats = mutableListOf<Stat>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initUi()
    }

    private fun initObserver() {
        mainViewModel.stats.observe(viewLifecycleOwner, Observer { statList ->
            Log.d(TAG, "initObserver: $statList")
            stats.clear()
            stats.addAll(statList)
            statsAdapter.notifyDataSetChanged()
        })
    }

    private fun initUi() {
        statsAdapter = StatsAdapter(stats)
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView = binding.recyclerViewStatsData
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = statsAdapter

        startingDate = binding.textViewStatsStartDate
        endingDate = binding.textViewStatsEndDate

        val calendar = Calendar.getInstance()
        val currentDateAndTime = DateAndTime.fromCalendar(calendar)
        val sevenDaysAgo = currentDateAndTime.day - 7

        startingDate.text = getString(R.string.formatted_date,sevenDaysAgo,currentDateAndTime.month+1,currentDateAndTime.year)
        endingDate.text = getString(R.string.formatted_date,currentDateAndTime.day,currentDateAndTime.month+1,currentDateAndTime.year)

        startingDate.setOnClickListener {
            val setDateAndTime = DateAndTime.fromString(startingDate.text.toString())
            DatePickerDialogFragment(this, setDateAndTime.day, setDateAndTime.month-1, setDateAndTime.year,
                STARTING_DATE_PICKER
            ).show(parentFragmentManager,"datePicker")
        }
        endingDate.setOnClickListener {
            val setDateAndTime = DateAndTime.fromString(endingDate.text.toString())
            DatePickerDialogFragment(this, setDateAndTime.day, setDateAndTime.month-1, setDateAndTime.year,
                ENDING_DATE_PICKER
            ).show(parentFragmentManager,"datePicker")
        }

        addStats = binding.fabStatsAddStat
        addStats.setOnClickListener(addStatOnClickListener)
    }

    private val addStatOnClickListener = View.OnClickListener {
        addStatFragment = AddStatFragment()
        addStatFragment.show(requireActivity().supportFragmentManager, "addStatFragment")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            view?.let {
                when(view.tag) {
                    STARTING_DATE_PICKER -> {
                        setDate(dayOfMonth,month,year,STARTING_DATE_PICKER)
                    }
                    ENDING_DATE_PICKER -> {
                        setDate(dayOfMonth,month,year, ENDING_DATE_PICKER)
                    }
                    ADD_STAT_DATE_PICKER -> {
                        // invalid
                    }
                }
            }
    }

    private fun setDate(day: Int, month: Int, year: Int, tag: String) {
        if(tag == STARTING_DATE_PICKER) {
            val starting = DateAndTime.fromString(getString(R.string.formatted_date,day,month,year))
            val ending = DateAndTime.fromString(endingDate.text.toString())
            if(starting > ending) {
                startingDate.text = endingDate.text
                Snackbar.make(requireView(),R.string.errorMessage_stats_invalidDate,Snackbar.LENGTH_SHORT).show()
            } else {
                startingDate.text = getString(R.string.formatted_date,day,month+1,year)
            }
        } else {
            val ending = DateAndTime.fromString(getString(R.string.formatted_date,day,month,year))
            val starting = DateAndTime.fromString(startingDate.text.toString())
            if(starting > ending) {
                endingDate.text = startingDate.text
                Snackbar.make(requireView(),R.string.errorMessage_stats_invalidDate,Snackbar.LENGTH_SHORT).show()
            } else {
                endingDate.text = getString(R.string.formatted_date,day,month+1,year)
            }
        }
    }

    companion object {
        private const val TAG = "StatsFragment"
    }

}