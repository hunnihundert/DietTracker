package com.hooni.diettracker.repository

import com.hooni.diettracker.data.Stat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeStatsRepository: Repository {

    private val fakeStats = mutableListOf<Stat>()
    private var idCounter = 1

    override fun getAllStats(): Flow<List<Stat>> {
        return flow {
            emit(fakeStats)
        }
    }

    override suspend fun insertStat(stat: Stat) {
        val newStat = Stat(stat.weight,stat.waist,stat.kCal,stat.date,stat.time, idCounter)
        idCounter++
        fakeStats.add(newStat)
    }

    override suspend fun deleteStat(stat: Stat) {
        fakeStats.remove(stat)
    }

    override suspend fun updateStat(stat: Stat) {
        if(fakeStats.removeIf { it.id == stat.id }) {
            fakeStats.add(stat)
        } else {
            return
        }
    }
}