package com.hooni.diettracker.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.hooni.diettracker.repository.FakeStatsRepositoryAndroidTest
import com.hooni.diettracker.data.dao.StatsDao
import com.hooni.diettracker.data.database.StatsDatabase
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidTestDatabaseModule = module {
    single { provideFakeStatsDatabase(get()) }
    single { provideFakeStatsDao(get()) }
}

val androidTestViewModelModule = module {
    viewModel { provideFakeMainViewModel(get(),androidApplication()) }
}

val androidTestRepositoryModule = module {
    single { provideFakeStatsRepositoryAndroidTest() }
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

private fun provideFakeMainViewModel(fakeStatsRepositoryAndroidTest: FakeStatsRepositoryAndroidTest, application: Application): MainViewModel {
    return MainViewModel(fakeStatsRepositoryAndroidTest, application)
}

private fun provideFakeStatsRepositoryAndroidTest(): FakeStatsRepositoryAndroidTest {
    return FakeStatsRepositoryAndroidTest()
}