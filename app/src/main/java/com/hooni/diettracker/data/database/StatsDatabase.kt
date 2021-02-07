package com.hooni.diettracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.data.dao.StatsDao

@Database(entities = [Stat::class],version = 5, exportSchema = false)
abstract class StatsDatabase: RoomDatabase() {
    abstract fun provideStatsDao(): StatsDao
}