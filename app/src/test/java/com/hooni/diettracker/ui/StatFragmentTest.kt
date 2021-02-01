package com.hooni.diettracker.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.common.truth.Truth.assertThat
import com.hooni.diettracker.R
import com.hooni.diettracker.di.testRepositoryModule
import com.hooni.diettracker.di.testViewModelModule
import com.hooni.diettracker.getOrAwaitValue
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StatFragmentTest : KoinTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val mainViewModel: MainViewModel by inject()

    private lateinit var scenario: FragmentScenario<StatsFragment>

    @Before
    fun setUp() {
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
        }
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
        mainViewModel.insertStat("70.2","88.5","2300","date")
        mainViewModel.insertStat("70.2","88.5","2300","date")
        mainViewModel.insertStat("70.2","88.5","2300","date")
        val listOfStats = mainViewModel.stats.getOrAwaitValue()
        assertThat(listOfStats.size).isEqualTo(3)
    }
}