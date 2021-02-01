package com.hooni.diettracker.data.dao

import androidx.room.*
import com.hooni.diettracker.data.Stat
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {

    @Query("SELECT * FROM stat")
    fun getAllStats(): Flow<List<Stat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStat(stat: Stat)

    @Delete
    suspend fun deleteStat(stat: Stat)

    @Update
    suspend fun updateStat(stat: Stat)
}