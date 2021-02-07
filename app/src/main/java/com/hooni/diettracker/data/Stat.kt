package com.hooni.diettracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stat(
    val weight: Double,
    val waist: Double,
    val kCal: Double,
    val date: String,
    val time: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)