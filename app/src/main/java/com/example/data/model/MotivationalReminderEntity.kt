package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motivational_reminders")
data class MotivationalReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val scheduledTime: String, // e.g., "08:00 AM"
    val period: String, // "Morning", "Midday", "Evening", "Night"
    val isEnabled: Boolean = true
)
