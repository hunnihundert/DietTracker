package com.hooni.diettracker.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.hooni.diettracker.data.dao.StatsDao
import com.hooni.diettracker.data.database.StatsDatabase
import com.hooni.diettracker.repository.FakeStatsRepository
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val testDatabaseModule = module {
    single { provideFakeStatsDatabase(get()) }
    single { provideFakeStatsDao(get()) }
}

val testViewModelModule = module {
    single { provideFakeMainViewModel(get(),androidApplication()) }
}

val testRepositoryModule = module {
    single { provideFakeStatsRepository() }
}

private fun provideFakeStatsDatabase(applicationContext: Context): StatsDatabase {
    return Room.inMemoryDatabaseBuilder(
        applicationContext,
        StatsDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()
}

private fun provideFakeStatsDao(statsDatabase: StatsDatabase): StatsDao {
    return statsDatabase.provideStatsDao()
}

private fun provideFakeMainViewModel(fakeStatsRepository: FakeStatsRepository, application: Application): MainViewModel {
    return MainViewModel(fakeStatsRepository, application)
}

private fun provideFakeStatsRepository(): FakeStatsRepository {
    return FakeStatsRepository()
}