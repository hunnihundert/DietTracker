package com.hooni.diettracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.data.dao.StatsDao

@Database(entities = [Stat::class],version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StatsDatabase: RoomDatabase() {
    abstract fun provideStatsDao(): StatsDao
}