package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_check_ins")
data class DailyCheckInEntity(
    @PrimaryKey
    val date: String, // "YYYY-MM-DD"
    val mood: String = "Happy", // "Happy", "Loved", "Calm", "Okay", "Tired", "Sad", "Stressed"
    val sleepHours: Float = 7.5f,
    val weather: String = "Sunny", // "Sunny", "Partly cloudy", "Cloudy", "Rainy", "Stormy", "Windy", "Snowy"
    val reflectionNote: String = ""
)
