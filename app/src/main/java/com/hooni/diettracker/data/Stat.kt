package com.hooni.diettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hooni.diettracker.util.DateAndTime

@Entity
data class Stat(
    val weight: Double,
    val waist: Double,
    val kCal: Double,
    val dateAndTime: DateAndTime,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)