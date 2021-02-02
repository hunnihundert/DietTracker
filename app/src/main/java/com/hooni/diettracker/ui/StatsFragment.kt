package com.hooni.diettracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.databinding.FragmentStatsBinding
import com.hooni.diettracker.ui.adapter.StatsAdapter
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding
    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var recyclerView: RecyclerView
    private lateinit var statsAdapter: StatsAdapter
    private lateinit var layoutManager: LinearLayoutManager

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
        mainViewModel.stats.observe(viewLifecycleOwner, Observer{ statList ->
            stats.addAll(statList)
        })
    }

    private fun initUi() {
        statsAdapter = StatsAdapter(stats)
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView = binding.recyclerViewStatsData
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = statsAdapter

        addStats = binding.fabStatsAddStat
        addStats.setOnClickListener(addStatOnClickListener)
    }

    private val addStatOnClickListener = View.OnClickListener {
        addStatFragment = AddStatFragment()
        addStatFragment.show(requireActivity().supportFragmentManager,"addStatFragment")
    }



}