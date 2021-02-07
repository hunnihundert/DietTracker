package com.hooni.diettracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.hooni.diettracker.R
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.repository.Repository
import com.hooni.diettracker.util.DateAndTime
import com.hooni.diettracker.util.Event
import com.hooni.diettracker.util.Resource
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository, application: Application) :
    AndroidViewModel(application) {

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
    val time = MutableLiveData<String>()

    /**
     * Calls insertStat in repository via coroutine in viewModelScope.
     *
     * @param stat Stat to be added to database
     */
    private fun insertStatIntoDatabase(stat: Stat) {
        viewModelScope.launch {
            repository.insertStat(stat)
        }
    }

    internal fun deleteStat(stat: Stat) {
        viewModelScope.launch {
            repository.deleteStat(stat)
        }
    }

    internal fun updateStat(stat: Stat) {
        viewModelScope.launch {
            repository.updateStat(stat)
        }
    }

    /**
     * Creates a new stat from input and calls [insertStatIntoDatabase].
     * Sets Event to error when any of the parameters is empty or cannot be converted to a double (except for date).
     *
     * @param weight Weight of the stat. Must be convertible to [Double].
     * @param waist Waist of the stat. Must be convertible to [Double].
     * @param kCal KCal of the stat. Must be convertible to [Double].
     * @param date Date of the stat. Currently, dd.mm.yy
     * @param time Time of the stat. 24hr format
     */
    internal fun insertStat(weight: String, waist: String, kCal: String, date: String, time: String) {
        if (weight.isEmpty() || waist.isEmpty() || kCal.isEmpty() || date.isEmpty() || time.isEmpty()) {
            _insertStatStatus.value =
                Event(Resource.error(R.string.errorMessage_viewModel_emptyField, null))
            return
        } else if (weight.toDoubleOrNull() == null || waist.toDoubleOrNull() == null || kCal.toDoubleOrNull() == null) {
            _insertStatStatus.value =
                Event(Resource.error(R.string.errorMessage_viewModel_invalidInput, null))
        } else {
            val newStat = Stat(weight.toDouble(), waist.toDouble(), kCal.toDouble(), date, time)
            insertStatIntoDatabase(newStat)
            _insertStatStatus.value = Event(Resource.success(newStat))
        }
    }

    /**
     * Calls [insertStat] with the stats that are currently in the corresponding live data.
     */
    internal fun insertStat() {
        insertStat(weight.value ?: "", waist.value ?: "", kCal.value ?: "", date.value ?: "", time.value ?: "")
    }

    /**
     * Sets the time in MainViewModel
     *
     * @param hourOfDay Hour of the day (24hr)
     * @param minute minute
     */
    internal fun setTime(hourOfDay: Int, minute: Int) {
        time.value = getApplication<Application>().resources.getString(
            R.string.formatted_time,
            hourOfDay,
            minute
        )
    }

    /**
     * Sets the date in MainViewModel
     *
     * @param dayOfMonth day of the month
     * @param month month (0: January, 11: December)
     * @param year year
     */
    internal fun setDate(dayOfMonth: Int, month: Int, year: Int) {
        date.value = getApplication<Application>().resources.getString(
            R.string.formatted_date,
            dayOfMonth,
            month,
            year
        )
    }

    /**
     * Sets the date and time value in the viewModel.
     * The info in the viewModel for both is a string and formatted as dd.mm.yy for date and hh:mm in 24hr format for time.
     *
     * @param dateAndTime Date and Time to set the info in the viewModel to.
     */
    internal fun setDateAndTime(dateAndTime: DateAndTime) {
        date.value = getApplication<Application>().resources.getString(
            R.string.formatted_date,
            dateAndTime.day,
            dateAndTime.month,
            dateAndTime.year
        )
        time.value = getApplication<Application>().resources.getString(
            R.string.formatted_time,
            dateAndTime.hour,
            dateAndTime.minute
        )
    }
}