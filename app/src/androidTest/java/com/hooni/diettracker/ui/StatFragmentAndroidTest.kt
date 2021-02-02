package com.hooni.diettracker.ui

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hooni.diettracker.R
import com.hooni.diettracker.di.androidTestRepositoryModule
import com.hooni.diettracker.di.androidTestViewModelModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import java.util.*

@RunWith(AndroidJUnit4::class)
class StatFragmentAndroidTest: KoinTest {

    private lateinit var scenario: FragmentScenario<StatsFragment>
    private lateinit var instrumentationContext: Context

    @Before
    fun setUp() {
        instrumentationContext = ApplicationProvider.getApplicationContext()
        startKoin{
            androidContext(instrumentationContext)
        }
        loadKoinModules(listOf(androidTestViewModelModule, androidTestRepositoryModule))
        scenario = launchFragmentInContainer<StatsFragment>(themeResId = R.style.Theme_DietTracker)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun whenTappingTheAddButtonInStatsFragmentTheAddFragmentDialogWillOpen() {

        onView(withId(R.id.fab_stats_addStat)).perform(click())
        onView(withId(R.id.textView_input_title)).check(matches(isDisplayed()))
    }

    @Test
    fun whenTappingTheAddButtonInStatsFragmentTheCurrentTimeAndDateWillShowInTimeAndDateFields() {
        onView(withId(R.id.fab_stats_addStat)).perform(click())
        val calendar = Calendar.getInstance()
        val currentTime = instrumentationContext.getString(R.string.formatted_time, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        val currentDate = instrumentationContext.getString(R.string.formatted_date, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))
        onView(withId(R.id.textView_input_time)).check(matches(withText(currentTime)))
        onView(withId(R.id.textView_input_date)).check(matches(withText(currentDate)))
    }

    @Test
    fun openingAddStatFragmentWhenEnteringInformationAndPressingConfirmThenStatIsVisibleInList() {

    }

    @Test
    fun openingAddStatFragmentWhenLeavingOneFieldEmptyAndPressingConfirmThenSnackBarAndCorrespondingFieldShow() {

    }

    @Test
    fun openingAddStatFragmentWhenTappingDateFieldThenDatePickerWillShow() {

    }

    @Test
    fun openingAddStatFragmentWhenTappingTimeFieldThenTimePickerWillShow() {

    }
}