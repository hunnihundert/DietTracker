package com.hooni.diettracker.data.database

import androidx.room.TypeConverter
import com.hooni.diettracker.util.DateAndTime

class Converters {
    @TypeConverter
    fun fromStringToDateAndTime(dateAndTimeString: String): DateAndTime {
        val date = dateAndTimeString.substringBefore("///")
        val time = dateAndTimeString.substringAfter("///")
        return DateAndTime.fromString(date, time)
    }

    @TypeConverter
    fun fromDateAndTimeToString(dateAndTime: DateAndTime): String {
        val date = dateAndTime.getDateString()
        val time = dateAndTime.getTimeString()
        return "$date///$time"
    }
}