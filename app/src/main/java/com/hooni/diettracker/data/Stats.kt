package com.hooni.diettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stats(
    val weight: Double,
    val waist: Double,
    val kCal: Double,
    val date: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 1
)