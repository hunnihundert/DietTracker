package com.hooni.diettracker.ui

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hooni.diettracker.R
import com.hooni.diettracker.di.androidTestRepositoryModule
import com.hooni.diettracker.di.androidTestViewModelModule
import com.hooni.diettracker.util.DateAndTime
import org.hamcrest.CustomMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import java.util.*

@RunWith(AndroidJUnit4::class)
class StatFragmentAndroidTest: KoinTest {

    private lateinit var scenario: FragmentScenario<StatsFragment>
    private lateinit var instrumentationContext: Context

    @Before
    fun setUp() {
        instrumentationContext = ApplicationProvider.getApplicationContext()
        loadKoinModules(listOf(androidTestViewModelModule,androidTestRepositoryModule))
        scenario = launchFragmentInContainer<StatsFragment>(themeResId = R.style.Theme_DietTracker)
    }

    @After
    fun tearDown() {
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
        val currentTime = instrumentationContext.getString(
            R.string.formatted_time, calendar.get(
                Calendar.HOUR_OF_DAY
            ), calendar.get(Calendar.MINUTE)
        )
        val currentDate = instrumentationContext.getString(
            R.string.formatted_date, calendar.get(
                Calendar.DAY_OF_MONTH
            ), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)
        )
        onView(withId(R.id.textView_input_time)).check(matches(withText(currentTime)))
        onView(withId(R.id.textView_input_date)).check(matches(withText(currentDate)))
    }

    @Test
    fun givenFreshStart_whenSeeingTheStatsFragment_thenTodayAnd7DaysEarlierIsShownInStartingEndingDate() {
        val calendar = Calendar.getInstance()
        val dateAndTime = DateAndTime.fromCalendar(calendar)
        val formattedDateString = instrumentationContext.getString(R.string.formatted_date,dateAndTime.day,dateAndTime.month,dateAndTime.year)

        onView(withId(R.id.textView_stats_startDate)).check(matches(withText(formattedDateString)))
    }

    @Test
    fun whenTappingTheStartingDateInStatFragment_thenTheDatePickerShowsUp() {
        onView(withId(R.id.textView_stats_startDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).check(matches(
            isDisplayed()))
    }

    @Test
    fun whenTappingTheEndingDateInStatFragment_thenTheDatePickerShowsUp() {
        onView(withId(R.id.textView_stats_startDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).check(matches(
            isDisplayed()))
    }

    @Test
    fun whenTappingTheStartDateInStatsFragmentAndSelectADateInTheDatePicker_thenTheStartDateChangesInStatsFragment() {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        var currentYear = calendar.get(Calendar.YEAR)
        var currentMonthMinusOne = calendar.get(Calendar.MONTH)
        if(currentMonthMinusOne == 0) {
            currentYear -= 1
            currentMonthMinusOne = 11
        }

        onView(withId(R.id.textView_stats_startDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).perform(PickerActions.setDate(currentYear, currentMonthMinusOne, currentDay))
        onView(withText("OK")).perform(click())
        val selectedDate = "$currentDay.$currentMonthMinusOne.$currentYear"
        onView(customWithText(R.id.textView_stats_startDate)).check(matches(withText(selectedDate)))
    }

    fun customWithText(resourceId: Int): Matcher<View?>? {
        return object : BoundedMatcher<View?, TextView>(TextView::class.java) {
            private var resourceName: String? = null
            private var expectedText: String? = null
            override fun describeTo(description: Description) {
                description.appendText("with string from resource id: ")
                description.appendValue(resourceId)
                if (null != resourceName) {
                    description.appendText("[")
                    description.appendText(resourceName)
                    description.appendText("]")
                }
                if (null != expectedText) {
                    description.appendText(" value: ")
                    description.appendText(expectedText)
                }
            }

            override fun matchesSafely(textView: TextView): Boolean {
                if (null == expectedText) {
                    try {
                        expectedText = textView.resources.getString(
                            resourceId
                        )
                        resourceName = textView.resources
                            .getResourceEntryName(resourceId)
                    } catch (ignored: Resources.NotFoundException) {
                        /*
                         * view could be from a context unaware of the resource
                         * id.
                         */
                    }
                }
                return if (null != expectedText) {
                    Log.d(TAG, "matchesSafely: ${textView.text}")
                    expectedText == textView.text
                } else {
                    false
                }
            }
        }
    }

    companion object {
        private const val TAG = "StatFragmentAndroidTest"
    }


}