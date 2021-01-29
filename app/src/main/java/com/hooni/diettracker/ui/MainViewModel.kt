package com.hooni.diettracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hooni.diettracker.data.Stats
import com.hooni.diettracker.repository.StatsRepository

class MainViewModel(private val repository: StatsRepository): ViewModel() {

    val stats: LiveData<List<Stats>>
        get() = _stats

    private val _stats = repository.getAllStats().asLiveData()
}