package com.hooni.diettracker.di

import android.content.Context
import androidx.room.Room
import com.hooni.diettracker.data.dao.StatsDao
import com.hooni.diettracker.data.database.StatsDatabase
import com.hooni.diettracker.repository.FakeStatsRepository
import com.hooni.diettracker.ui.FakeMainViewModel
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val testDatabaseModule = module {
    single { provideFakeStatsDatabase(get()) }
    single { provideFakeStatsDao(get()) }
}

val testViewModelModule = module {
    single { provideFakeMainViewModel(get()) }
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

private fun provideFakeMainViewModel(fakeStatsRepository: FakeStatsRepository): MainViewModel {
    return MainViewModel(fakeStatsRepository)
}

private fun provideFakeStatsRepository(): FakeStatsRepository {
    return FakeStatsRepository()
}