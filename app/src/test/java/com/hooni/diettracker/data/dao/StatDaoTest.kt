package com.hooni.diettracker.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.hooni.diettracker.data.Stat
import com.hooni.diettracker.data.database.StatsDatabase
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

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class StatDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: StatsDatabase
    private lateinit var dao: StatsDao

    private val mockStat1 = Stat(71.1,81.1,2111.1,"date1",1)
    private val mockStat2 = Stat(42.2,82.2,2222.2,"date2",2)
    private val mockStat3 = Stat(53.3,83.3,2333.3,"date3",3)


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
        dao.insertStat(mockStat1)
        val allStats = dao.getAllStats().first()
        assertThat(allStats).contains(mockStat1)
    }

    @Test
    fun insertItemsAndDeleteItemsFromDatabase() = runBlockingTest {
        dao.insertStat(mockStat1)
        dao.insertStat(mockStat2)
        dao.insertStat(mockStat3)
        var statList = dao.getAllStats().first()
        assertThat(statList).contains(mockStat1)

        dao.deleteStat(mockStat1)
        statList = dao.getAllStats().first()
        assertThat(statList).doesNotContain(mockStat1)
        assertThat(statList).containsExactly(mockStat2,mockStat3)

        dao.deleteStat(mockStat2)
        statList = dao.getAllStats().first()
        assertThat(statList).doesNotContain(mockStat2)
        assertThat(statList).containsExactly(mockStat3)

        dao.deleteStat(mockStat3)
        statList = dao.getAllStats().first()
        assertThat(statList).isEmpty()
    }

    @Test
    fun insertItemsIntoDatabase() = runBlockingTest {
        dao.insertStat(mockStat1)
        dao.insertStat(mockStat2)
        dao.insertStat(mockStat3)
        val allStats = dao.getAllStats().first()
        assertThat(allStats).containsExactly(mockStat1, mockStat2, mockStat3)
    }

    @Test
    fun insertItemAndUpdateItemInDatabase() = runBlockingTest {
        dao.insertStat(mockStat1)
        val updatedWeight = 55.5
        val updatedKCal = 2222.2
        val updatedStat = Stat(updatedWeight,81.1,updatedKCal,"date1",1)
        dao.updateStat(updatedStat)
        val allStats = dao.getAllStats().first()
        assertThat(allStats[0].weight).isEqualTo(updatedWeight)
        assertThat(allStats[0].kCal).isEqualTo(updatedKCal)
    }

    @Test
    fun insertItemAndUpdateNonExistingItem() = runBlockingTest{
        dao.insertStat(mockStat1)
        dao.updateStat(mockStat2)
        val allStats = dao.getAllStats().first()
        assertThat(allStats[0]).isEqualTo(mockStat1)
    }
}