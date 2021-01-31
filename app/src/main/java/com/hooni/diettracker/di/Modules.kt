package com.hooni.diettracker.di

import android.content.Context
import androidx.room.Room
import com.hooni.diettracker.data.dao.StatsDao
import com.hooni.diettracker.data.database.StatsDatabase
import com.hooni.diettracker.repository.StatsRepository
import com.hooni.diettracker.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { provideMainViewModel(get()) }
}

val repositoryModule = module {
    single { provideRepository(get()) }
}

val databaseModule = module {
    single { provideStatsDatabase(get()) }
    single { provideStatsDao(get()) }
}

private fun provideMainViewModel(statsRepository: StatsRepository): MainViewModel {
    return MainViewModel(statsRepository)
}

private fun provideRepository(statsDao: StatsDao): StatsRepository {
    return StatsRepository(statsDao)
}

private fun provideStatsDatabase(applicationContext: Context): StatsDatabase {
    return Room.databaseBuilder(
        applicationContext,
        StatsDatabase::class.java,
        "stats-database"
    ).build()
}

private fun provideStatsDao(statsDatabase: StatsDatabase): StatsDao {
    return statsDatabase.provideStatsDao()
}