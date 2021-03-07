package com.hooni.diettracker.util

import java.util.*

data class DateAndTime(
    var day: Int,
    var month: Int,
    var year: Int,
    var hour: Int,
    var minute: Int
) : Comparable<DateAndTime> {

    override fun compareTo(other: DateAndTime): Int {
        if (year < other.year) return -1
        if (year > other.year) return 1
        if (month < other.month) return -1
        if (month > other.month) return 1
        if (day < other.day) return -1
        if (day > other.day) return 1
        if (hour < other.hour) return -1
        if (hour > other.hour) return 1
        if (minute < other.minute) return -1
        if (minute > other.minute) return 1
        return 0
    }

    fun getDateString(): String {
        return "$day.$month.$year"
    }

    fun getTimeString(): String {
        return "$hour:$minute"
    }

    /**
     * Creates a new DateAndTime object with the changed value. If a value is left empty it will
     * adopt the value from the current DateAndTime object
     *
     * @param changedDay optional
     * @param changedMonth optional
     * @param changedYear optional
     * @param changedHour optional
     * @param changedMinute optional
     */
    fun changeElement(
        changedDay: Int = this.day,
        changedMonth: Int = this.month,
        changedYear: Int = this.year,
        changedHour: Int = this.hour,
        changedMinute: Int = this.minute
    ): DateAndTime {
        return DateAndTime(changedDay, changedMonth, changedYear, changedHour, changedMinute)
    }

    fun reduceBy(amount: Int, unit: Units) {
        if (this.day == 1 && this.month == 1 && this.year == 1970) return
        when (unit) {
            Units.DAY -> {
                if (this.day > amount) {
                    this.day -= amount
                } else {
                    if (this.day == 1) {
                        this.reduceBy(1, Units.MONTH)
                        when (this.month) {
                            1, 3, 5, 7, 8, 10, 12 -> {
                                this.day = 31
                            }
                            4, 6, 9, 11 -> {
                                this.day = 30
                            }
                            else -> {
                                if (this.year % 4 != 0 || (this.year % 100 == 0 && this.year % 400 == 0)) {
                                    this.day = 28
                                } else {
                                    this.day = 29
                                }
                            }
                        }
                        this.reduceBy(amount - 1, Units.DAY)
                    } else {
                        val daysToReduceThisMonth = this.day - 1
                        this.day = 1
                        this.reduceBy(amount - daysToReduceThisMonth, Units.DAY)
                    }
                }
            }
            Units.MONTH -> {
                if (this.month > amount) {
                    this.month -= amount
                } else {
                    var monthToSubtract: Int
                    var yearsToSubtract = 0
                    if (amount > 12) {
                        monthToSubtract = amount % 12
                        yearsToSubtract = (amount - monthToSubtract) / 12
                    } else {
                        monthToSubtract = amount
                    }
                    if (monthToSubtract >= this.month) {
                        yearsToSubtract++
                        monthToSubtract -= this.month
                        this.month = 12
                    }
                    this.month -= monthToSubtract
                    this.reduceBy(yearsToSubtract, Units.YEAR)
                }
            }
            Units.YEAR -> {
                if (this.year - amount < 1970) {
                    this.year = 1970
                } else {
                    this.year -= amount
                }
            }
            Units.HOUR -> {
                if (this.hour >= amount) {
                    this.hour -= amount
                } else {
                    var hoursToSubtract = 0
                    var daysToSubtract = 0
                    if (amount > 24) {
                        hoursToSubtract = amount % 24
                        daysToSubtract = (amount - hoursToSubtract) / 24

                    } else {
                        hoursToSubtract = amount
                    }
                    if (hoursToSubtract >= this.hour) {
                        daysToSubtract++
                        hoursToSubtract -= this.hour
                        this.hour = 24
                    }
                    this.reduceBy(daysToSubtract, Units.DAY)
                    this.reduceBy(hoursToSubtract, Units.HOUR)
                }
            }
            Units.MINUTE -> {
                if (this.minute >= amount) {
                    this.minute -= amount
                } else {
                    var minutesToSubtract = 0
                    var hoursToSubtract = 0
                    if (amount > 60) {
                        minutesToSubtract = amount % 60
                        hoursToSubtract = (amount - minutesToSubtract) / 60
                    } else {
                        minutesToSubtract = amount
                    }
                    if (minutesToSubtract >= this.minute) {
                        hoursToSubtract++
                        minutesToSubtract -= this.minute
                        this.minute = 60
                    }
                    this.reduceBy(hoursToSubtract, Units.HOUR)
                    this.reduceBy(minutesToSubtract, Units.MINUTE)
                }
            }
        }
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
                currentMonth + 1,
                currentYear,
                currentHour,
                currentMinute
            )
        }

        fun fromString(date: String = "", time: String = ""): DateAndTime {
            val dateToSet = if (date == "") "01.01.1970" else date
            val timeToSet = if (time == "") "00:00" else time

            val day = dateToSet.substringBefore(".").trim().toInt()
            val month = dateToSet.substringAfter(".").substringBefore(".").trim().toInt()
            val year = dateToSet.substringAfterLast(".").trim().toInt()

            val hour = timeToSet.substringBefore(":").trim().toInt()
            val minute = timeToSet.substringAfter(":").trim().toInt()

            return DateAndTime(day, month, year, hour, minute)
        }

        fun fromDateAndTime(otherDateAndTime: DateAndTime): DateAndTime {
            return DateAndTime(otherDateAndTime.day, otherDateAndTime.month, otherDateAndTime.year, otherDateAndTime.hour, otherDateAndTime.minute)
        }
    }

    /** Changes the date by days/month/years/hours/minutes
     *
     *  @param amount Amount of units by which the date will be changed.
     *  @param unit Unit by which the date will be changed. {@link com.hooni.diettracker.util.DateAndTime.Units}
     *
     *  @return Returns a new DateAndTime object with modified date/time.
     */

    enum class Units {
        DAY,
        MONTH,
        YEAR,
        HOUR,
        MINUTE
    }
}