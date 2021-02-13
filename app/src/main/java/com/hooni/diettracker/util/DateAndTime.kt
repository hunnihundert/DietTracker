package com.hooni.diettracker.util

import java.util.*

data class DateAndTime(
    val day: Int,
    val month: Int,
    val year: Int,
    val hour: Int,
    val minute: Int
): Comparable<DateAndTime> {

    override fun compareTo(other: DateAndTime): Int {
        if(year < other.year) return -1
        if(year > other.year) return 1
        if(month < other.month) return -1
        if(month > other.month) return 1
        if(day < other.day) return -1
        if(day > other.day) return 1
        if(hour < other.hour) return -1
        if(hour > other.hour) return 1
        if(minute < other.minute) return -1
        if(minute > other.minute) return 1
        return 0
    }

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

        fun fromString(date: String = "", time: String = ""): DateAndTime {
            val dateToSet = if(date=="") "01.01.1970" else date
            val timeToSet = if(time=="") "00:00" else time

            val day = dateToSet.substringBefore(".").trim().toInt()
            val month = dateToSet.substringAfter(".").substringBefore(".").trim().toInt()
            val year = dateToSet.substringAfterLast(".").trim().toInt()

            val hour = timeToSet.substringBefore(":").trim().toInt()
            val minute = timeToSet.substringAfter(":").trim().toInt()

            return DateAndTime(day, month, year, hour, minute)

        }
    }

    /** Changes the date by days/month/years/hours/minutes
     *
     *  @param amount Amount of units by which the date will be changed.
     *  @param unit Unit by which the date will be changed. {@link com.hooni.diettracker.util.DateAndTime.Units}
     *
     *  @return Returns a new DateAndTime object with modified date/time.
     */
    fun DateAndTime.reduceBy(amount: Int, unit: Units) {
        when(unit) {
            Units.DAY -> {}
            Units.MONTH -> {}
            Units.YEAR -> {}
            Units.HOUR -> {}
            Units.MINUTE -> {}
        }
    }

    enum class Units {
        DAY,
        MONTH,
        YEAR,
        HOUR,
        MINUTE
    }
}