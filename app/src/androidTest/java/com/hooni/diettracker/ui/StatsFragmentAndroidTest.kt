package com.hooni.diettracker.ui

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hooni.diettracker.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatsFragmentTest {
    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    lateinit var mockMainViewModel: MainViewModel

    @Before
    fun setup() {
        mockMainViewModel = mock()
    }
}