package com.hooni.diettracker.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hooni.diettracker.di.viewModelModule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class StatFragmentTest: KoinTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        startKoin {
            listOf(viewModelModule)
        }


    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `check if given no stats viewModel returns an empty list`() {

    }
}