package com.hooni.diettracker.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.data.dao.StatsDao
import com.hooni.diettracker.data.database.StatsDatabase
import com.hooni.diettracker.repository.StatsRepository
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class StatFragmentAndroidTest: AutoCloseKoinTest() {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val instrumentationContext = getApplicationContext<Context>()

    val statsDatabase: StatsDatabase by inject()
    val statsDao: StatsDao by inject()
    val statsRepository: StatsRepository by inject()
    val mainViewModel: MainViewModel by inject()

    @Before
    fun setUp() {

        val roomTestModule = module {
            single {
                Room.inMemoryDatabaseBuilder(instrumentationContext, StatsDatabase::class.java)
                    .allowMainThreadQueries()
                    .build()
            }
        }

        val repositoryModule = module {
            single{StatsRepository(get())}
        }

        val viewModelModule = module {
            viewModel {
                MainViewModel(get())
            }
        }
        stopKoin()
        startKoin {
            listOf(
                roomTestModule,
                repositoryModule,
                viewModelModule
            )
        }
    }

    @After
    fun tearDown() {
        statsDatabase.close()
        stopKoin()
    }

    @Test
    fun checkIfGivenNoStatsViewModelReturnsEmptyList() {
        var emptyList = listOf(Stat(99.9,88.8, 5555.0,"date"))
        emptyList = mainViewModel.stat.getOrAwaitValue()
         assertThat(emptyList).isEmpty()
    }
}