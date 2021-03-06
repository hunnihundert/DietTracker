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
    private lateinit var date6: DateAndTime
    private lateinit var date7: DateAndTime

    @Before
    fun setup() {
        date1 = DateAndTime(1,1,2000,0,0)
        date2 = DateAndTime(1,2,1999,0,0)
        date3 = DateAndTime(8,5,2005,0,0)
        date4 = DateAndTime(1,1,2000,0,0)
        date5 = DateAndTime(1,1,2000,14,0)
        date6 = DateAndTime(1,5,2005,0,0)
        date7 = DateAndTime(15,3,1996,0,0)
    }

    @Test
    fun compareTo() {
        assertThat(date1 > date2).isTrue()
        assertThat(date2 < date3).isTrue()
        assertThat(date1 == date4).isTrue()
        assertThat(date1 < date5).isTrue()
    }

    @Test
    fun `reduce date by 1 day`() {
        date2.reduceBy(1, DateAndTime.Units.DAY)
        val compareDate = DateAndTime(31,1,1999,0,0)
        assertThat(date2).isEqualTo(compareDate)
    }

    @Test
    fun `reduce date by 7 days`() {
        date3.reduceBy(7, DateAndTime.Units.DAY)
        assertThat(date3).isEqualTo(date6)
    }

    @Test
    fun `reduce date by 7 days into the previous month`() {
        date2.reduceBy(7, DateAndTime.Units.DAY)
        val compareDate = DateAndTime(25,1,1999,0,0)
        assertThat(date2).isEqualTo(compareDate)
    }

    @Test
    fun `reduce date by 7 days into the previous year`() {
        date1.reduceBy(7,DateAndTime.Units.DAY)
        val compareDate = DateAndTime(25,12,1999,0,0)
        assertThat(date1).isEqualTo(compareDate)
    }

    @Test
    fun `reduce date by 30 days in a leap year from march to february`() {
        date7.reduceBy(30,DateAndTime.Units.DAY)
        val compareDate = DateAndTime(14,2,1996,0,0)
        assertThat(date7).isEqualTo(compareDate)
    }

    @Test
    fun `reduce date by 65 days`() {
        date3.reduceBy(65, DateAndTime.Units.DAY)
        val compareDate = DateAndTime(4,3,2005,0,0)
        assertThat(date3).isEqualTo(compareDate)
    }

    @Test
    fun `reduce year to earlier than 1970`() {
        date1.reduceBy(100, DateAndTime.Units.YEAR)
        assertThat(date1.year).isEqualTo(1970)
    }

    @Test
    fun `reduce year by 10 years`() {
        date1.reduceBy(10, DateAndTime.Units.YEAR)
        val compareDate = DateAndTime(1,1,1990,0,0)
        assertThat(date1).isEqualTo(compareDate)
    }

    @Test
    fun `reduce month by 1`() {
        date3.reduceBy(1, DateAndTime.Units.MONTH)
        val compareDate = DateAndTime(8,4,2005,0,0)
        assertThat(date3).isEqualTo(compareDate)
    }

    @Test
    fun `reduce month by 15 month`() {
        date1.reduceBy(15, DateAndTime.Units.MONTH)
        val compareDate = DateAndTime(1,10,1998,0,0)
        assertThat(date1).isEqualTo(compareDate)
    }

    @Test
    fun `reduce days by 500000 days`() {
        date1.reduceBy(500000,DateAndTime.Units.DAY)
        val compareDate = DateAndTime(1,1,1970,0,0)
        assertThat(date1).isEqualTo(compareDate)
    }

}