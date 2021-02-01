package com.hooni.diettracker.ui.viewmodel

import androidx.lifecycle.*
import com.hooni.diettracker.R
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.repository.Repository
import com.hooni.diettracker.util.Event
import com.hooni.diettracker.util.Resource
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository): ViewModel() {

    private val _stats = repository.getAllStats().asLiveData()
    val stats: LiveData<List<Stat>>
        get() = _stats

    private val _insertStatStatus = MutableLiveData<Event<Resource<Stat>>>()
    val insertStatStatus: LiveData<Event<Resource<Stat>>>
        get() = _insertStatStatus

    val weight = MutableLiveData<String>()
    val waist = MutableLiveData<String>()
    val kCal = MutableLiveData<String>()
    val date = MutableLiveData<String>()

    private fun insertStatIntoDatabase(stat: Stat) {
        viewModelScope.launch {
            repository.insertStat(stat)
        }
    }

    fun deleteStat(stat: Stat) {
        viewModelScope.launch {
            repository.deleteStat(stat)
        }
    }

    fun updateStat(stat: Stat) {
        viewModelScope.launch {
            repository.updateStat(stat)
        }
    }


    fun insertStat(weight: String, waist: String, kCal: String, date: String) {
        if(weight.isEmpty() || waist.isEmpty() || kCal.isEmpty() || date.isEmpty()){
            _insertStatStatus.value = Event(Resource.error(R.string.errorMessage_viewModel_emptyField,null))
            return
        }
        else if(weight.toDoubleOrNull() == null || waist.toDoubleOrNull() == null || kCal.toDoubleOrNull() == null) {
            _insertStatStatus.value = Event(Resource.error(R.string.errorMessage_viewModel_invalidInput,null))
        } else {
            val newStat = Stat(weight.toDouble(), waist.toDouble(), kCal.toDouble(),date)
            insertStatIntoDatabase(newStat)
            _insertStatStatus.value = Event(Resource.success(newStat))
        }
    }

}