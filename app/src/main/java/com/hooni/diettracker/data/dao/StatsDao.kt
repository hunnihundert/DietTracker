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

    @Query("DELETE FROM stat WHERE id IN (:idList)")
    suspend fun deleteStats(idList: List<Int>)

    @Update
    suspend fun updateStat(stat: Stat)
}