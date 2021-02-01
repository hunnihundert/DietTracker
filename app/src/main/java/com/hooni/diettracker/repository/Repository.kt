package com.hooni.diettracker.repository

import com.hooni.diettracker.data.Stat
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getAllStats(): Flow<List<Stat>>

    suspend fun insertStat(stat: Stat)

    suspend fun deleteStat(stat: Stat)

    suspend fun updateStat(stat: Stat)
}