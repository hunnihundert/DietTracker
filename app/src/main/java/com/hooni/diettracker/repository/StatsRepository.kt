package com.hooni.diettracker.repository

import com.hooni.diettracker.data.Stats
import com.hooni.diettracker.data.dao.StatsDao
import kotlinx.coroutines.flow.Flow

class StatsRepository(private val statsDao: StatsDao) {

    fun getAllStats(): Flow<List<Stats>> {
        return statsDao.getAllStats()
    }
}