package com.hooni.diettracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hooni.diettracker.data.Stats
import com.hooni.diettracker.data.dao.StatsDao

@Database(entities = [Stats::class],version = 1, exportSchema = false)
abstract class StatsDatabase: RoomDatabase() {
    abstract fun provideStatsDao(): StatsDao
}