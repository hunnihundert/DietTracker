package com.hooni.diettracker.util

import java.util.*

data class DateAndTime(
    val day: Int,
    val month: Int,
    val year: Int,
    val hour: Int,
    val minute: Int
) {

    companion object {
        fun fromCalendar(calendar: Calendar): DateAndTime {
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentDayOfTheMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)
            return DateAndTime(
                currentDayOfTheMonth,
                currentMonth,
                currentYear,
                currentHour,
                currentMinute
            )
        }
    }
}