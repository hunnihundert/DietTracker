package com.hooni.diettracker.repository

import com.hooni.diettracker.data.Stat
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getAllStats(): Flow<List<Stat>>

    suspend fun insertStat(stat: Stat)

    suspend fun deleteStats(idList: List<Int>)

    suspend fun updateStat(stat: Stat)
}