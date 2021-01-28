package com.hooni.diettracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hooni.diettracker.data.Stats
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {

    @Query("SELECT * FROM stats")
    fun getAllStats(): Flow<List<Stats>>

    @Insert
    fun insertStats(stats: Stats)

    @Delete
    fun deleteStats(stats: Stats)
}