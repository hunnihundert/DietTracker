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
import java.util.*

class MainViewModel(private val repository: Repository, application: Application) :
    AndroidViewModel(application) {

    private val _stats = repository.getAllStats().asLiveData()
    internal val stats: LiveData<List<Stat>>
        get() = _stats

    private val _insertStatStatus = MutableLiveData<Event<Resource<Stat>>>()
    internal val insertStatStatus: LiveData<Event<Resource<Stat>>>
        get() = _insertStatStatus

    internal var editStatId: Int? = null

    val weight = MutableLiveData<String>()
    val waist = MutableLiveData<String>()
    val kCal = MutableLiveData<String>()

    private val dateTime = MutableLiveData<DateAndTime?>()
    val date = dateTime.switchMap {
        if(it == null) {
            MutableLiveData("")
        } else {
            MutableLiveData(it.getDateString())
        }

    }
    val time = dateTime.switchMap {
        if(it == null) {
            MutableLiveData("")
        } else {
            MutableLiveData(it.getTimeString())
        }
    }

    private val _startingDate = MutableLiveData<String>()
    internal val startingDate: LiveData<String>
        get() = _startingDate

    private val _endingDate = MutableLiveData<String>()
    internal val endingDate: LiveData<String>
        get() = _endingDate

    private val _dateInputError = MutableLiveData<Event<String>>()
    internal val dateInputError: LiveData<Event<String>>
        get() = _dateInputError

    private val deletionList = mutableListOf<Stat>()

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

    private fun deleteStat(stats: List<Stat>) {
        val deletionIdList = stats.map { stat ->
            stat.id
        }
        viewModelScope.launch {
            repository.deleteStats(deletionIdList)
            deletionList.clear()
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

            val newStat: Stat
            val dateAndTime = DateAndTime.fromString(date, time)
            if(editStatId == null) {
                newStat = Stat(weight.toDouble(), waist.toDouble(), kCal.toDouble(), dateAndTime)
                insertStatIntoDatabase(newStat)
            } else {
                newStat = Stat(weight.toDouble(), waist.toDouble(), kCal.toDouble(), dateAndTime, editStatId!!)
                updateStat(newStat)
                editStatId = null
            }
            _insertStatStatus.value = Event(Resource.success(newStat))
        }
    }

    /**
     * Calls [insertStat] with the stats that are currently in the corresponding live data.
     */
    internal fun insertStat() {
        val date = dateTime.value?.getDateString() ?: ""
        val time = dateTime.value?.getTimeString() ?: ""
        insertStat(weight.value ?: "", waist.value ?: "", kCal.value ?: "", date, time)
    }

    /**
     * Sets the time in MainViewModel
     *
     * @param hourOfDay Hour of the day (24hr)
     * @param minute minute
     */
    internal fun setTime(hourOfDay: Int, minute: Int) {
        dateTime.value = dateTime.value!!.changeElement(changedHour = hourOfDay, changedMinute = minute)
    }

    /**
     * Sets the date in MainViewModel
     *
     * @param dayOfMonth day of the month
     * @param month month (0: January, 11: December)
     * @param year year
     */
    internal fun setDate(dayOfMonth: Int, month: Int, year: Int) {
        dateTime.value = dateTime.value!!.changeElement(dayOfMonth,month,year)
    }

    /**
     * Sets the date and time value in the viewModel.
     *
     * @param dateAndTime Date and Time to set the info in the viewModel to.
     */
    internal fun setDateAndTime(dateAndTime: DateAndTime) {
        dateTime.value = dateAndTime
    }

    /**
     * Gets the date and time from the viewModel.
     * If not instantiated it gets a DateAndTime object based on the current date and time
     */
    internal fun getDateAndTime(): DateAndTime {
        if(dateTime.value == null) {
            dateTime.value = DateAndTime.fromCalendar(Calendar.getInstance())
        }
        
        return dateTime.value!!
    }

    internal fun setStartingDate(day: Int, month: Int, year: Int) {
        val newStartingDate = getApplication<Application>().resources.getString(R.string.formatted_date,day,month,year)
        val newStarting = DateAndTime.fromString(newStartingDate)
        val ending = DateAndTime.fromString(_endingDate.value ?: getApplication<Application>().resources.getString(R.string.formatted_date,day+1,month,year))
        if(newStarting > ending) {
            _dateInputError.value = Event(getApplication<Application>().resources.getString(R.string.errorMessage_stats_invalidDate))
        } else {
            _startingDate.value = newStartingDate
        }
    }

    internal fun setEndingDate(day: Int, month: Int, year: Int) {
        val newEndingDate = getApplication<Application>().resources.getString(R.string.formatted_date,day,month,year)
        val newEnding = DateAndTime.fromString(newEndingDate)
        val starting = DateAndTime.fromString(_startingDate.value ?: getApplication<Application>().resources.getString(R.string.formatted_date,day-1,month,year))
        if(newEnding < starting) {
            _dateInputError.value = Event(getApplication<Application>().resources.getString(R.string.errorMessage_stats_invalidDate))
        } else {
            _endingDate.value = newEndingDate
        }
    }

    /**
     * Clears weight, waist, kCal, date, time in the viewModel.
     */
    internal fun clearStats() {
        weight.value = ""
        waist.value = ""
        kCal.value = ""
        dateTime.value = null
    }

    internal fun addOrRemoveFromDeletionList(stat: Stat, isSelected: Boolean) {
        if(isSelected) {
            deletionList.add(stat)
        } else {
            deletionList.remove(stat)
        }
    }

    internal fun getSelectedItemSize(): Int {
        return deletionList.size
    }

    internal fun deleteSelectedItems() {
        deleteStat(deletionList)
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}