package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AIChatMessageEntity
import com.example.data.model.DailyCheckInEntity
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.MotivationalReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // --- Habits ---
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY id ASC")
    fun getAllHabitsFlow(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY id ASC")
    suspend fun getAllHabits(): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE category = :category AND isArchived = 0 ORDER BY id ASC")
    fun getHabitsByCategoryFlow(category: String): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    // --- Logs ---
    @Query("SELECT * FROM habit_logs WHERE date = :date")
    fun getLogsForDateFlow(date: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE date = :date")
    suspend fun getLogsForDate(date: String): List<HabitLogEntity>

    @Query("SELECT * FROM habit_logs WHERE date LIKE :monthPrefix || '%'")
    fun getLogsForMonthFlow(monthPrefix: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId")
    suspend fun getLogsForHabit(habitId: Long): List<HabitLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun deleteLog(habitId: Long, date: String)

    @Query("DELETE FROM habit_logs")
    suspend fun clearAllLogs()

    @Query("DELETE FROM habit_logs WHERE date = :date")
    suspend fun clearLogsForDate(date: String)

    // --- Daily Check-Ins (Mood, Sleep, Weather, Reflections) ---
    @Query("SELECT * FROM daily_check_ins WHERE date = :date")
    fun getCheckInForDateFlow(date: String): Flow<DailyCheckInEntity?>

    @Query("SELECT * FROM daily_check_ins WHERE date = :date")
    suspend fun getCheckInForDate(date: String): DailyCheckInEntity?

    @Query("SELECT * FROM daily_check_ins WHERE date LIKE :monthPrefix || '%'")
    fun getCheckInsForMonthFlow(monthPrefix: String): Flow<List<DailyCheckInEntity>>

    @Query("SELECT * FROM daily_check_ins ORDER BY date DESC LIMIT 30")
    suspend fun getRecentCheckIns(): List<DailyCheckInEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCheckIn(checkIn: DailyCheckInEntity)

    // --- Motivational Reminders ---
    @Query("SELECT * FROM motivational_reminders ORDER BY id ASC")
    fun getAllRemindersFlow(): Flow<List<MotivationalReminderEntity>>

    @Query("SELECT * FROM motivational_reminders WHERE isEnabled = 1")
    suspend fun getActiveReminders(): List<MotivationalReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<MotivationalReminderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: MotivationalReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: MotivationalReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: MotivationalReminderEntity)

    // --- AI Chat Messages ---
    @Query("SELECT * FROM ai_chat_messages ORDER BY timestamp ASC")
    fun getChatMessagesFlow(): Flow<List<AIChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: AIChatMessageEntity): Long

    @Query("DELETE FROM ai_chat_messages")
    suspend fun clearChat()
}
