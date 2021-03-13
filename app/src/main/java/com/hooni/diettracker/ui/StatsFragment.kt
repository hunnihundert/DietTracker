package com.hooni.diettracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
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
import com.hooni.diettracker.ui.adapter.StatsItemDetailsLookup
import com.hooni.diettracker.ui.pickerdialogs.DatePickerDialogFragment
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import com.hooni.diettracker.util.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

// TODO: recyclerview liste anhand von datum sortieren

class StatsFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: FragmentStatsBinding
    private val mainViewModel: MainViewModel by sharedViewModel()

    private lateinit var graph: LineChart

    private lateinit var recyclerView: RecyclerView
    private lateinit var statsAdapter: StatsAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var startingDate: TextView
    private lateinit var endingDate: TextView
    private lateinit var noDataTextView: TextView

    private lateinit var addStats: FloatingActionButton

    private lateinit var addStatFragment: DialogFragment

    private lateinit var selectionTracker: SelectionTracker<Long>
    private var actionMode: ActionMode? = null
    private lateinit var actionModeCallback: ActionMode.Callback

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
        initActionModeCallBack()
        initRecyclerView()
        initTextViews()
        initAddStatFab()

    }

    private fun initActionModeCallBack() {
        actionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater = mode.menuInflater
                inflater.inflate(R.menu.action_mode_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_mode_delete -> {
                        mainViewModel.deleteSelectedItems()
                        selectionTracker.clearSelection()
                        mode.finish()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                mode?.finish()
                selectionTracker.clearSelection()
                actionMode = null
            }

        }
    }

    private fun initRecyclerView() {
        val editClickListener: (Stat) -> Unit = { stat ->
            val weight = stat.weight
            val waist = stat.waist
            val kCal = stat.kCal
            val dateAndTime = stat.dateAndTime

            mainViewModel.setDateAndTime(dateAndTime)
            mainViewModel.weight.value = weight.toString()
            mainViewModel.waist.value = waist.toString()
            mainViewModel.kCal.value = kCal.toString()
            mainViewModel.editStatId = stat.id

            addStatFragment = AddStatFragment()
            addStatFragment.show(
                requireActivity().supportFragmentManager,
                ADD_STAT_FRAGMENT_EDITING
            )
        }
        statsAdapter = StatsAdapter(stats, editClickListener)

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

        selectionTracker = SelectionTracker.Builder(
            "selectionId",
            recyclerView,
            StableIdKeyProvider(recyclerView),
            StatsItemDetailsLookup(recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()


        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if (mainViewModel.getSelectedItemSize() == 1 && actionMode == null) {
                    actionMode = requireActivity().startActionMode(actionModeCallback)
                }

            }

            override fun onItemStateChanged(key: Long, selected: Boolean) {
                val selectedStat = statsAdapter.displayedStats[key.toInt()]
                mainViewModel.addOrRemoveFromDeletionList(selectedStat, selected)
                if (mainViewModel.getSelectedItemSize() == 0) {
                    actionMode?.finish()
                }
            }
        })
        statsAdapter.tracker = selectionTracker
    }

    private fun initTextViews() {
        startingDate = binding.textViewStatsStartDate
        endingDate = binding.textViewStatsEndDate
        noDataTextView = binding.textViewStatsNoResults

        val calendar = Calendar.getInstance()

        val currentDateAndTime = DateAndTime.fromCalendar(calendar)
        val sevenDaysAgo = DateAndTime.fromDateAndTime(currentDateAndTime)
        sevenDaysAgo.reduceBy(7, DateAndTime.Units.DAY)

        mainViewModel.setStartingDate(
            sevenDaysAgo.day,
            sevenDaysAgo.month,
            sevenDaysAgo.year
        )
        mainViewModel.setEndingDate(
            currentDateAndTime.day,
            currentDateAndTime.month,
            currentDateAndTime.year
        )

        startingDate.setOnClickListener {
            val setDateAndTime = DateAndTime.fromString(startingDate.text.toString())
            DatePickerDialogFragment(
                this, setDateAndTime.day, setDateAndTime.month, setDateAndTime.year,
                STARTING_DATE_PICKER
            ).show(parentFragmentManager, DATE_PICKER)
        }
        endingDate.setOnClickListener {
            val setDateAndTime = DateAndTime.fromString(endingDate.text.toString())
            DatePickerDialogFragment(
                this, setDateAndTime.day, setDateAndTime.month, setDateAndTime.year,
                ENDING_DATE_PICKER
            ).show(parentFragmentManager, DATE_PICKER)
        }
    }

    private fun initAddStatFab() {
        addStats = binding.fabStatsAddStat
        addStats.setOnClickListener(addStatOnClickListener)
    }

    private val addStatOnClickListener = View.OnClickListener {
        addStatFragment = AddStatFragment()
        addStatFragment.show(requireActivity().supportFragmentManager, ADD_STAT_FRAGMENT_ADDING)
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
        graph.xAxis.position = XAxis.XAxisPosition.BOTTOM
    }

    private fun updateGraphData() {
        setGraphData()
        graph.notifyDataSetChanged()
        graph.invalidate()
    }

    private fun setGraphData() {
        val waistEntries = mutableListOf<Entry>()
        val weightEntries = mutableListOf<Entry>()

        val startDateAndTime = DateAndTime.fromString(startingDate.text.toString())
        val endDateAndTime = DateAndTime.fromString(endingDate.text.toString(),LAST_TIME_OF_THE_DAY)

        val statsWithinDateRange = stats.filter {
            it.dateAndTime in startDateAndTime..endDateAndTime
        }

        statsWithinDateRange.forEachIndexed { index, stat ->
            val waistEntry = Entry(index.toFloat(), stat.waist.toFloat())
            val weightEntry = Entry(index.toFloat(), stat.weight.toFloat())
            waistEntries.add(waistEntry)
            weightEntries.add(weightEntry)
        }

        val xAxisDescriptiveValues = statsWithinDateRange.map { stat ->
            stat.dateAndTime.getDateString().substringBeforeLast(".")
        }

        val xAxisValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                if (value < 0f || value.toInt() >= xAxisDescriptiveValues.size) {
                    if (xAxisDescriptiveValues.size == 1) return ""
                    return super.getAxisLabel(value, axis)
                }
                return xAxisDescriptiveValues[value.toInt()]
            }
        }

        val leftYAxisValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return "%.1f".format(value)
            }
        }

        val xAxis = graph.xAxis
        val leftYAxis = graph.axisLeft
        val rightYAxis = graph.axisRight
        xAxis.granularity = 0.1f
        xAxis.valueFormatter = xAxisValueFormatter
        leftYAxis.granularity = 0.1f
        leftYAxis.valueFormatter = leftYAxisValueFormatter
        leftYAxis.setDrawGridLines(false)
        rightYAxis.setDrawGridLines(false)

        val waistLineDataSet = LineDataSet(waistEntries, "Waist")
        val weightLineDataSet = LineDataSet(weightEntries, "Weight")
        weightLineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        val waistDataSet = mutableListOf<ILineDataSet>()
        waistLineDataSet.color = R.color.design_default_color_primary
        waistDataSet.add(waistLineDataSet)
        waistDataSet.add(weightLineDataSet)
        graph.data = LineData(waistDataSet)

    }

    private fun initObserver() {
        mainViewModel.stats.observe(viewLifecycleOwner, { statList ->
            stats.clear()
            stats.addAll(statList)
            stats.sortBy { it.dateAndTime }
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
            if (!errorEvent.hasBeenHandled) {
                Snackbar.make(
                    requireView(),
                    errorEvent.getContentIfNotHandled().toString(),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateDateFilter() {
        val newFilterDate = "${startingDate.text}///${endingDate.text}"
        statsAdapter.filter.filter(newFilterDate)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        view?.let {
            when (view.tag) {
                STARTING_DATE_PICKER -> {
                    mainViewModel.setStartingDate(dayOfMonth, month + 1, year)
                }
                ENDING_DATE_PICKER -> {
                    mainViewModel.setEndingDate(dayOfMonth, month + 1, year)
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