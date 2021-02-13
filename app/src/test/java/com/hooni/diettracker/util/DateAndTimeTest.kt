package com.hooni.diettracker.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.Before

class DateAndTimeTest {

    private lateinit var date1: DateAndTime
    private lateinit var date2: DateAndTime
    private lateinit var date3: DateAndTime
    private lateinit var date4: DateAndTime
    private lateinit var date5: DateAndTime

    @Before
    fun setup() {
        date1 = DateAndTime(1,1,2000,0,0)
        date2 = DateAndTime(1,2,1999,0,0)
        date3 = DateAndTime(2,5,2005,0,0)
        date4 = DateAndTime(1,1,2000,0,0)
        date5 = DateAndTime(1,1,2000,14,0)

    }

    @Test
    fun compareTo() {
        assertThat(date1 > date2).isTrue()
        assertThat(date2 < date3).isTrue()
        assertThat(date1 == date4).isTrue()
        assertThat(date1 < date5).isTrue()
    }
}