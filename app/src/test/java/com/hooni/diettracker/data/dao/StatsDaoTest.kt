package com.hooni.diettracker.data.dao

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.hooni.diettracker.data.Stats
import com.hooni.diettracker.data.database.StatsDatabase
import com.hooni.diettracker.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class StatsDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: StatsDatabase
    private lateinit var dao: StatsDao

    private val mockStats1 = Stats(71.1,81.1,2111.1,"date1",1)
    private val mockStats2 = Stats(42.2,82.2,2222.2,"date2",2)
    private val mockStats3 = Stats(53.3,83.3,2333.3,"date3",3)


    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StatsDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = database.provideStatsDao()
    }

    @After
    fun tearDown() {
        database.close()
        stopKoin()
    }

    @Test
    fun insertItemIntoDatabase() = runBlockingTest {
        dao.insertStats(mockStats1)
        val allStats = dao.getAllStats().first()
        assertThat(allStats).contains(mockStats1)
    }

    @Test
    fun insertAndDeleteItemFromDatabase() = runBlockingTest {
        dao.insertStats(mockStats1)
        dao.insertStats(mockStats2)
        dao.insertStats(mockStats3)
        var statList = dao.getAllStats().first()
        assertThat(statList).contains(mockStats1)

        dao.deleteStats(mockStats1)
        statList = dao.getAllStats().first()
        assertThat(statList).doesNotContain(mockStats1)
        assertThat(statList).containsExactly(mockStats2,mockStats3)

        dao.deleteStats(mockStats2)
        statList = dao.getAllStats().first()
        assertThat(statList).doesNotContain(mockStats2)
        assertThat(statList).containsExactly(mockStats3)

        dao.deleteStats(mockStats3)
        statList = dao.getAllStats().first()
        assertThat(statList).isEmpty()
    }

    @Test
    fun insertItemsAndGetAllStatsFromDatabase() = runBlockingTest {
        dao.insertStats(mockStats1)
        dao.insertStats(mockStats2)
        dao.insertStats(mockStats3)
        val allStats = dao.getAllStats().first()
        assertThat(allStats).containsExactly(mockStats1, mockStats2, mockStats3)
    }

    @Test
    fun insertItemAndUpdate() = runBlockingTest {
        dao.insertStats(mockStats1)
        val updatedWeight = 55.5
        val updatedKCal = 2222.2
        val updatedStat = Stats(updatedWeight,81.1,updatedKCal,"date1",1)
        dao.updateStats(updatedStat)
        val allStats = dao.getAllStats().first()
        assertThat(allStats[0].weight).isEqualTo(updatedWeight)
        assertThat(allStats[0].kCal).isEqualTo(updatedKCal)
    }
}