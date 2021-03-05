package com.hooni.diettracker.repository

import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.data.dao.StatsDao
import kotlinx.coroutines.flow.Flow

class StatsRepository(private val statsDao: StatsDao): Repository {

    override fun getAllStats(): Flow<List<Stat>> {
        return statsDao.getAllStats()
    }

    override suspend fun insertStat(stat: Stat){
        statsDao.insertStat(stat)
    }

    override suspend fun deleteStats(idList: List<Int>) {
        statsDao.deleteStats(idList)
    }

    override suspend fun updateStat(stat: Stat) {
        statsDao.updateStat(stat)
    }
}