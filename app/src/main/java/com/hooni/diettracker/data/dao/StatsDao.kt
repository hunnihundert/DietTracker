package com.hooni.diettracker.data.dao

import androidx.room.*
import com.hooni.diettracker.data.Stats
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {

    @Query("SELECT * FROM stats")
    fun getAllStats(): Flow<List<Stats>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: Stats)

    @Delete
    suspend fun deleteStats(stats: Stats)

    @Update
    suspend fun updateStats(stats: Stats)
}