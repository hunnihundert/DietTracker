package com.hooni.diettracker.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.hooni.diettracker.data.Stat
import kotlinx.coroutines.flow.Flow

class FakeStatsRepositoryAndroidTest: Repository {

    private val underlyingList = mutableListOf<Stat>()
    private val fakeStats = MutableLiveData(underlyingList)
    private var idCounter = 1

    override fun getAllStats(): Flow<List<Stat>> {
        return fakeStats.asFlow()
    }

    override suspend fun insertStat(stat: Stat) {
        val newStat = Stat(stat.weight,stat.waist,stat.kCal,stat.date,stat.time,idCounter)
        idCounter++
        underlyingList.add(newStat)

    }

    override suspend fun deleteStat(stat: Stat) {
        underlyingList.remove(stat)
    }

    override suspend fun updateStat(stat: Stat) {
        if(underlyingList.removeIf { it.id == stat.id }) {
            underlyingList.add(stat)
        } else {
            return
        }
    }
}