package com.hooni.diettracker.di

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.hooni.diettracker.data.dao.StatsDao
import com.hooni.diettracker.data.database.StatsDatabase
import org.koin.dsl.module

val testDatabaseModule = module {
    single { provideTestStatsDatabase(get()) }
    single { provideTestStatsDao(get()) }
}

private fun provideTestStatsDatabase(applicationContext: Context): StatsDatabase {
    return Room.inMemoryDatabaseBuilder(
        applicationContext,
        StatsDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()
}

private fun provideTestStatsDao(statsDatabase: StatsDatabase): StatsDao {
    return statsDatabase.provideStatsDao()
}