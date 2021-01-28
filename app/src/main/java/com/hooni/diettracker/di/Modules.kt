package com.hooni.diettracker.di

import android.content.Context
import androidx.room.Room
import com.hooni.diettracker.data.dao.StatsDao
import com.hooni.diettracker.data.database.StatsDatabase
import com.hooni.diettracker.repository.DataRepository
import com.hooni.diettracker.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { provideMainViewModel() }
}

val repositoryModule = module {
    single { provideRepository() }
}

val databaseModule = module {
    single { provideStatsDatabase(get()) }
    single { provideStatsDao(get()) }

}

private fun provideMainViewModel(): MainViewModel {
    return MainViewModel()
}

private fun provideRepository(): DataRepository {
    return DataRepository()
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