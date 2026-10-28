package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "daily", "weekly", "monthly"
    val timeOfDay: String, // "morning", "afternoon", "evening", "anytime"
    val iconName: String, // "water", "bed", "sun", "book", "run", "meditate", "clean", "phone", "family", "coffee"
    val targetGoal: Int = 30, // target completions per month/period
    val colorHex: String = "#38BDF8",
    val createdDate: String,
    val isArchived: Boolean = false
)
