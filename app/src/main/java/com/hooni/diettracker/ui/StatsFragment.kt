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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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

    private lateinit var graph: LineChart

    private lateinit var recyclerView: RecyclerView
    private lateinit var statsAdapter: StatsAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var startingDate: TextView
    private lateinit var endingDate: TextView
    private lateinit var noDataTextView: TextView

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
        initGraph()
    }

    private fun initUi() {
        initRecyclerView()
        initTextViews()
        initAddStatFab()
    }

    private fun initRecyclerView() {
        statsAdapter = StatsAdapter(stats)
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView = binding.recyclerViewStatsData
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = statsAdapter
        statsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (statsAdapter.displayedStats.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    noDataTextView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    noDataTextView.visibility = View.GONE
                }
            }
        })
    }

    private fun initTextViews() {
        startingDate = binding.textViewStatsStartDate
        endingDate = binding.textViewStatsEndDate
        noDataTextView = binding.textViewStatsNoResults

        val calendar = Calendar.getInstance()

        val currentDateAndTime = DateAndTime.fromCalendar(calendar)
        val sevenDaysAgo = currentDateAndTime.day - 7

        mainViewModel.setStartingDate(sevenDaysAgo,currentDateAndTime.month,currentDateAndTime.year)
        mainViewModel.setEndingDate(currentDateAndTime.day,currentDateAndTime.month,currentDateAndTime.year)

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
    }

    private fun initAddStatFab() {
        addStats = binding.fabStatsAddStat
        addStats.setOnClickListener(addStatOnClickListener)
    }

    private val addStatOnClickListener = View.OnClickListener {
        addStatFragment = AddStatFragment()
        addStatFragment.show(requireActivity().supportFragmentManager, "addStatFragment")
    }

    private fun initGraph() {
        graph = binding.chart
        setGraphGeneralStyling()
        setGraphAxisStyling()
        updateGraphData()
    }

    private fun setGraphGeneralStyling() {
        val graphDescription = Description()
        graphDescription.isEnabled = false
        graph.description = graphDescription
        graph.setNoDataText(getString(R.string.noDataGraphDescription_stats_emptyGraph))
        graph.setDrawBorders(true)
    }

    private fun setGraphAxisStyling() {
        graph.axisRight.isEnabled = false
        graph.xAxis.position = XAxis.XAxisPosition.BOTTOM
    }

    private fun updateGraphData() {
        setGraphData()
        graph.notifyDataSetChanged()
        graph.invalidate()
    }

    private fun setGraphData() {
        val entries = mutableListOf<Entry>()

        val startDateAndTime = DateAndTime.fromString(startingDate.text.toString())
        val endDateAndTime = DateAndTime.fromString(endingDate.text.toString())

        val resultList = stats.filter {
            DateAndTime.fromString(it.date) in startDateAndTime..endDateAndTime
        }

        resultList.forEachIndexed { index, stat ->
            val entry = Entry(index.toFloat(),stat.waist.toFloat())
            entries.add(entry)
        }
        val xAxisDescriptiveValues = resultList.map { stat ->
            stat.date.substringBeforeLast(".")
        }
        val valueFormatter = object: ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                if(value < 0f || value.toInt() >= xAxisDescriptiveValues.size) {
                    if(xAxisDescriptiveValues.size == 1) return ""
                    return super.getAxisLabel(value, axis)
                }
                return xAxisDescriptiveValues[value.toInt()]
            }
        }

        val xAxis = graph.xAxis
        xAxis.granularity = 1f
        xAxis.valueFormatter = valueFormatter

        val lineDataSet = LineDataSet(entries,"Waist")
        val dataSet = mutableListOf<ILineDataSet>()
        dataSet.add(lineDataSet)
        graph.data = LineData(dataSet)
    }

    private fun initObserver() {
        mainViewModel.stats.observe(viewLifecycleOwner, { statList ->
            stats.clear()
            stats.addAll(statList)
            statsAdapter.notifyDataSetChanged()
            updateDateFilter()
            updateGraphData()
        })

        mainViewModel.startingDate.observe(viewLifecycleOwner, { changedStartingDate ->
            startingDate.text = changedStartingDate
            updateDateFilter()
            updateGraphData()
        })

        mainViewModel.endingDate.observe(viewLifecycleOwner, { changedEndingDate ->
            endingDate.text = changedEndingDate
            updateDateFilter()
            updateGraphData()
        })

        mainViewModel.dateInputError.observe(viewLifecycleOwner, { errorEvent ->
            if(!errorEvent.hasBeenHandled) {
                Snackbar.make(requireView(),errorEvent.getContentIfNotHandled().toString(),Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateDateFilter() {
        val newFilterDate = "${startingDate.text}///${endingDate.text}"
        statsAdapter.filter.filter(newFilterDate)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            view?.let {
                when(view.tag) {
                    STARTING_DATE_PICKER -> {
                        mainViewModel.setStartingDate(dayOfMonth,month,year)
                    }
                    ENDING_DATE_PICKER -> {
                        mainViewModel.setEndingDate(dayOfMonth,month, year)
                    }
                    ADD_STAT_DATE_PICKER -> {
                        // invalid
                    }
                }
            }
    }

    companion object {
        private const val TAG = "StatsFragment"
    }

}