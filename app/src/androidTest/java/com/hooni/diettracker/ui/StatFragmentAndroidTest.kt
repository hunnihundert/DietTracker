package com.hooni.diettracker.ui

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hooni.diettracker.R
import com.hooni.diettracker.di.androidTestRepositoryModule
import com.hooni.diettracker.di.androidTestViewModelModule
import com.hooni.diettracker.ui.adapter.StatsViewHolder
import com.hooni.diettracker.util.DateAndTime
import org.hamcrest.CoreMatchers.not
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
class StatFragmentAndroidTest : KoinTest {

    private lateinit var scenario: FragmentScenario<StatsFragment>
    private lateinit var instrumentationContext: Context

    @Before
    fun setUp() {
        instrumentationContext = ApplicationProvider.getApplicationContext()
        loadKoinModules(listOf(androidTestViewModelModule, androidTestRepositoryModule))
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_DietTracker)
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
            R.string.formatted_date, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR))
        onView(withId(R.id.textView_input_time)).check(matches(withText(currentTime)))
        onView(withId(R.id.textView_input_date)).check(matches(withText(currentDate)))
    }

    @Test
    fun givenFreshStart_whenSeeingTheStatsFragment_thenTodayAnd7DaysEarlierIsShownInStartingEndingDate() {
        val calendar = Calendar.getInstance()
        val dateAndTime = DateAndTime.fromCalendar(calendar)
        val formattedDateString = instrumentationContext.getString(
            R.string.formatted_date,
            dateAndTime.day-7,
            dateAndTime.month+1,
            dateAndTime.year
        )

        onView(withId(R.id.textView_stats_startDate)).check(matches(withText(formattedDateString)))
    }

    @Test
    fun whenTappingTheStartingDateInStatFragment_thenTheDatePickerShowsUp() {
        onView(withId(R.id.textView_stats_startDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun whenTappingTheEndingDateInStatFragment_thenTheDatePickerShowsUp() {
        onView(withId(R.id.textView_stats_endDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun whenTappingTheStartDateInStatsFragmentAndSelectADateInTheDatePicker_thenTheStartDateChangesInStatsFragment() {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        var currentYear = calendar.get(Calendar.YEAR)
        var currentMonthMinusOne = calendar.get(Calendar.MONTH)
        if (currentMonthMinusOne == 0) {
            currentYear -= 1
            currentMonthMinusOne = 11
        }

        onView(withId(R.id.textView_stats_startDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).perform(
            PickerActions.setDate(
                currentYear,
                currentMonthMinusOne,
                currentDay
            )
        )
        onView(withText("OK")).perform(click())
        val selectedDateAndTime = DateAndTime(currentDay, currentMonthMinusOne, currentYear, 0, 0)
        val selectedDate = instrumentationContext.getString(
            R.string.formatted_date,
            selectedDateAndTime.day,
            selectedDateAndTime.month,
            selectedDateAndTime.year
        )
        onView(withId(R.id.textView_stats_startDate)).check(matches(withText(selectedDate)))
    }

    @Test
    fun whenChangingTheDateRange_thenOnlyStatsWithinTheRangeAreVisible() {
        // select date range: 2021/1/1 - 2021/7/2
        onView(withId(R.id.textView_stats_startDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).perform(
            PickerActions.setDate(
                2021,
                1,
                1
            )
        )
        onView(withText("OK")).perform(click())
        onView(withId(R.id.textView_stats_endDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).perform(
            PickerActions.setDate(
                2021,
                7,
                2
            )
        )
        onView(withText("OK")).perform(click())

        onView(withId(R.id.recyclerView_stats_data)).check(matches(hasItem(hasDescendant(withText("Waist: 111.1")))))
        onView(withId(R.id.recyclerView_stats_data)).check(matches(hasItem(hasDescendant(withText("Waist: 222.2")))))
        onView(withId(R.id.recyclerView_stats_data)).check(matches(hasItem(hasDescendant(withText("Waist: 333.3")))))

        // select date range: 2021/1/1 - 2021/6/2
        onView(withId(R.id.textView_stats_startDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).perform(
            PickerActions.setDate(
                2021,
                1,
                1
            )
        )
        onView(withText("OK")).perform(click())
        onView(withId(R.id.textView_stats_endDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).perform(
            PickerActions.setDate(
                2021,
                6,
                2
            )
        )
        onView(withText("OK")).perform(click())

        onView(withId(R.id.recyclerView_stats_data)).check(matches(hasItem(hasDescendant(withText("Waist: 111.1")))))
        onView(withId(R.id.recyclerView_stats_data)).check(matches(hasItem(hasDescendant(withText("Waist: 222.2")))))
        onView(withId(R.id.recyclerView_stats_data)).check(matches(not(hasItem(hasDescendant(withText("Waist: 333.3")))))
        )

        // select date range: 2021/5/1 - 2021/6/2
        onView(withId(R.id.textView_stats_startDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).perform(
            PickerActions.setDate(
                2021,
                5,
                1
            )
        )
        onView(withText("OK")).perform(click())
        onView(withId(R.id.textView_stats_endDate)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).perform(
            PickerActions.setDate(
                2021,
                6,
                2
            )
        )
        onView(withText("OK")).perform(click())

        onView(withId(R.id.recyclerView_stats_data)).check(matches(not(hasItem(hasDescendant(withText("Waist: 111.1"))))))
        onView(withId(R.id.recyclerView_stats_data)).check(matches(hasItem(hasDescendant(withText("Waist: 222.2")))))
        onView(withId(R.id.recyclerView_stats_data)).check(
            matches(not(hasItem(hasDescendant(withText("Waist: 333.3")
                        )
                    )
                )
            )
        )
    }



    @Test
    fun whenTappingAnEntryInTheRecyclerView_thenAnEditPopUpAppearsWithTheCurrentStatsOfTheEntryEntered() {
        onView(withId(R.id.recyclerView_stats_data)).perform(RecyclerViewActions.actionOnItemAtPosition<StatsViewHolder>(0, click()))
        onView(withId(R.id.textView_input_title)).check(matches(isDisplayed()))
    }

    @Test
    fun whenTappingAndEntryChangingTheStatsAndConfirming_thenTheSelectedEntryStatsAreChanged() {

    }

    @Test
    fun whenTappingAndHoldingAnEntry_ThenTheActionMenuOpens() {

    }

    @Test
    fun whenTappingHoldingAnEntryAndTappingTheDeleteIcon_thenTheEntryIsDeletedFromTheRecyclerView() {

    }

    private fun hasItem(matcher: Matcher<View?>): Matcher<View?> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item: ")
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val adapter = view.adapter
                for (position in 0 until adapter!!.itemCount) {
                    val type = adapter.getItemViewType(position)
                    val holder = adapter.createViewHolder(view, type)
                    adapter.onBindViewHolder(holder, position)
                    if (matcher.matches(holder.itemView)) {
                        return true
                    }
                }
                return false
            }

            }
        }

    companion object {
        private const val TAG = "StatFragmentAndroidTest"
    }
}