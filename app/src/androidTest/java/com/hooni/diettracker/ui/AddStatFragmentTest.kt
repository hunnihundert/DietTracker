package com.hooni.diettracker.ui


import android.content.Context
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.hooni.diettracker.R
import com.hooni.diettracker.di.androidTestRepositoryModule
import com.hooni.diettracker.di.androidTestViewModelModule
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules

@RunWith(AndroidJUnit4::class)
class AddStatFragmentTest {

    private lateinit var scenario: FragmentScenario<AddStatFragment>
    private lateinit var instrumentationContext: Context
    private val typedInCorrectWeight = "70.2"
    private val typedInCorrectWaist = "89.1"
    private val typedInCorrectKCal = "2300"


    @Before
    fun setUp() {
        instrumentationContext = ApplicationProvider.getApplicationContext()
        loadKoinModules(listOf(androidTestViewModelModule, androidTestRepositoryModule))
        scenario = launchFragment(themeResId = R.style.Theme_DietTracker)
    }

    @Test
    fun whenEnteringAllInfoAndPressingConfirm_thenTheDialogDismisses() {
        scenario.onFragment { fragment ->
            assertThat(fragment.dialog).isNotNull()
            assertThat(fragment.requireDialog().isShowing).isTrue()
        }
        onView(withId(R.id.textInputEditText_input_weight)).perform(typeText(typedInCorrectWeight))
        onView(withId(R.id.textInputEditText_input_waist)).perform(typeText(typedInCorrectWaist))
        onView(withId(R.id.textInputEditText_input_kcal)).perform(typeText(typedInCorrectKCal))
        onView(withId(R.id.button_input_confirm)).perform(click())
        onView(withText("Weight")).check(doesNotExist())
    }

    @Test
    fun whenLeavingOneOfTheFieldsBlankAndPressingConfirm_thenASnackBarIsShownAndCorrespondingFieldsAreIndicated() {
        onView(withId(R.id.textInputEditText_input_weight)).perform(typeText(typedInCorrectWeight))
        onView(withId(R.id.textInputEditText_input_kcal)).perform(typeText(typedInCorrectKCal))
        onView(withId(R.id.button_input_confirm)).perform(click())
        onView(withText(instrumentationContext.resources.getString(R.string.errorMessage_viewModel_emptyField))).check(matches(isDisplayed()))
        onView(withText(instrumentationContext.resources.getString(R.string.errorMessage_viewModel_emptyField))).check(matches(isDisplayed()))

    }

    @Test
    fun whenTappingOnTheDateField_thenTheDatePickerIsShown() {
        onView(withId(R.id.textView_input_date)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.qualifiedName))).check(matches(
            isDisplayed()))
    }

    @Test
    fun whenTappingOnTheTimeField_thenTheTimePickerIsShown() {
        onView(withId(R.id.textView_input_time)).perform(click())
        onView(withClassName(Matchers.equalTo(TimePicker::class.qualifiedName))).check(matches(
            isDisplayed()))
    }
}