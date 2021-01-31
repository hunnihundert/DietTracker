package com.hooni.diettracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.databinding.FragmentStatsBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding
    private val mainViewModel: MainViewModel by viewModels()

    private val stats = mutableListOf<Stat>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
    }

    private fun initObserver() {
        mainViewModel.stats.observe(viewLifecycleOwner, Observer{ statList ->
            stats.addAll(statList)
        })
    }



}