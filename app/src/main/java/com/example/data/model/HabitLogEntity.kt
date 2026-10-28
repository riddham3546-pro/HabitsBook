package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "habit_logs",
    primaryKeys = ["habitId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["date"]), Index(value = ["habitId"])]
)
data class HabitLogEntity(
    val habitId: Long,
    val date: String, // "YYYY-MM-DD"
    val isCompleted: Boolean = true,
    val note: String? = null
)
