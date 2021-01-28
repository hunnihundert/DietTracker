package com.hooni.diettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stats(
    @PrimaryKey val id: Int = 0,
    val weight: Double,
    val waist: Double,
    val kCal: Double,
    val date: String
)