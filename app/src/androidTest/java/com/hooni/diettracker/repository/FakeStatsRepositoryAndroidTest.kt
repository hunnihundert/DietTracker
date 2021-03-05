package com.hooni.diettracker.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.hooni.diettracker.data.Stat
import kotlinx.coroutines.flow.Flow

class FakeStatsRepositoryAndroidTest: Repository {

    private val underlyingList = mutableListOf<Stat>()
    private val fakeStats = MutableLiveData(underlyingList)
    private var idCounter = 4

    init {
        val firstStat = Stat(111.1,111.1,111.1,"1.1.2021","12:00",1)
        val secondStat = Stat(222.2,222.2,222.2,"1.6.2021","12:00",2)
        val thirdStat = Stat(333.3,333.3,333.3,"1.7.2021","12:00",3)
        underlyingList.addAll(listOf(firstStat,secondStat,thirdStat))
    }

    override fun getAllStats(): Flow<List<Stat>> {
        return fakeStats.asFlow()
    }

    override suspend fun insertStat(stat: Stat) {
        val newStat = Stat(stat.weight,stat.waist,stat.kCal,stat.date,stat.time,idCounter)
        idCounter++
        underlyingList.add(newStat)

    }

    override suspend fun deleteStats(idList: List<Int>) {
        underlyingList.removeIf { stat ->
            idList.contains(stat.id)
        }
    }

    override suspend fun updateStat(stat: Stat) {
        if(underlyingList.removeIf { it.id == stat.id }) {
            underlyingList.add(stat)
        } else {
            return
        }
    }
}