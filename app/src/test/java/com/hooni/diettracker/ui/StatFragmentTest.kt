package com.hooni.diettracker.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.google.common.truth.Truth.assertThat
import com.hooni.diettracker.R
import com.hooni.diettracker.core.TestBaseApplication
import com.hooni.diettracker.di.testRepositoryModule
import com.hooni.diettracker.di.testViewModelModule
import com.hooni.diettracker.getOrAwaitValue
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestBaseApplication::class)
class StatFragmentTest : KoinTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val mainViewModel: MainViewModel by inject()

    private lateinit var scenario: FragmentScenario<StatsFragment>

    @Before
    fun setUp() {
        loadKoinModules(listOf(testViewModelModule, testRepositoryModule))
        scenario = launchFragmentInContainer<StatsFragment>(themeResId = R.style.Theme_DietTracker)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `given no stats when retrieving info from viewModel, then recyclerView shows an empty list`() {
        val listOfStats = mainViewModel.stats.getOrAwaitValue()
        assertThat(listOfStats).isEmpty()
    }

    @Test
    fun `given 3 stats when retrieving info from viewModel, then recyclerView shows a list with 3 elements`() {
        mainViewModel.insertStat("70.2","88.5","2300","date","time")
        mainViewModel.insertStat("70.2","88.5","2300","date","time")
        mainViewModel.insertStat("70.2","88.5","2300","date","time")
        val listOfStats = mainViewModel.stats.getOrAwaitValue()
        assertThat(listOfStats.size).isEqualTo(3)
    }
}