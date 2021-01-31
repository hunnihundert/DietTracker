package com.hooni.diettracker.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.hooni.diettracker.MainCoroutineRule
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.getOrAwaitValue
import com.hooni.diettracker.repository.FakeStatsRepository
import com.hooni.diettracker.util.Status
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineDispatcher = MainCoroutineRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(FakeStatsRepository())
    }


    @After
    fun tearDown() {
    }

    @Test
    fun `insert stat and leave one value blank`() {
        viewModel.insertStat("","1","2000","date")
        val insertStatStatus = viewModel.insertStatStatus.getOrAwaitValue()
        assertThat(insertStatStatus.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert stat and enter letters instead of numbers in values`(){
        viewModel.insertStat("","1","2000","date")
        val insertStatStatus = viewModel.insertStatStatus.getOrAwaitValue()
        assertThat(insertStatStatus.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `insert stat and check if it was added`() = runBlockingTest{
        launch(Dispatchers.Main) {
            viewModel.insertStat("79.8","90","2103.1","date")
            val allStats = viewModel.stats.getOrAwaitValue()
            val addedStat = Stat(79.8,90.0,2103.1,"date",1)
            assertThat(allStats).contains(addedStat)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `insert multiple stats and check if they were added`() = runBlockingTest{
        launch(Dispatchers.Main) {
            viewModel.insertStat("71.1","91","2111.1","date")
            viewModel.insertStat("72.2","92","2222.2","date2")
            viewModel.insertStat("73.3","93","2333.3","date3")
            val allStats = viewModel.stats.getOrAwaitValue()
            val addedStat1 = Stat(71.1,91.0,2111.1,"date", 1)
            val addedStat2 = Stat(72.2,92.0,2222.2,"date2", 2)
            val addedStat3 = Stat(73.3,93.0,2333.3,"date3", 3)
            assertThat(allStats).containsExactly(addedStat1, addedStat2, addedStat3)
        }
    }
}