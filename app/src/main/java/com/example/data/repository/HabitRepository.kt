package com.example.data.repository

import com.example.data.db.HabitDao
import com.example.data.model.AIChatMessageEntity
import com.example.data.model.DailyCheckInEntity
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.MotivationalReminderEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HabitRepository(private val dao: HabitDao) {

    val allHabitsFlow: Flow<List<HabitEntity>> = dao.getAllHabitsFlow()

    fun getLogsForDateFlow(date: String): Flow<List<HabitLogEntity>> =
        dao.getLogsForDateFlow(date)

    suspend fun getLogsForDate(date: String): List<HabitLogEntity> =
        dao.getLogsForDate(date)

    fun getLogsForMonthFlow(monthPrefix: String): Flow<List<HabitLogEntity>> =
        dao.getLogsForMonthFlow(monthPrefix)

    fun getCheckInForDateFlow(date: String): Flow<DailyCheckInEntity?> =
        dao.getCheckInForDateFlow(date)

    fun getCheckInsForMonthFlow(monthPrefix: String): Flow<List<DailyCheckInEntity>> =
        dao.getCheckInsForMonthFlow(monthPrefix)

    val remindersFlow: Flow<List<MotivationalReminderEntity>> =
        dao.getAllRemindersFlow()

    val chatMessagesFlow: Flow<List<AIChatMessageEntity>> =
        dao.getChatMessagesFlow()

    suspend fun toggleHabitCompletion(habitId: Long, date: String, currentlyCompleted: Boolean) {
        if (currentlyCompleted) {
            dao.deleteLog(habitId, date)
        } else {
            dao.insertLog(HabitLogEntity(habitId = habitId, date = date, isCompleted = true))
        }
    }

    suspend fun addHabit(habit: HabitEntity): Long {
        return dao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: HabitEntity) {
        dao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        dao.deleteHabit(habit)
    }

    suspend fun clearAllTicks() {
        dao.clearAllLogs()
    }

    suspend fun saveCheckIn(checkIn: DailyCheckInEntity) {
        dao.insertOrUpdateCheckIn(checkIn)
    }

    suspend fun updateReminder(reminder: MotivationalReminderEntity) {
        dao.updateReminder(reminder)
    }

    suspend fun addReminder(reminder: MotivationalReminderEntity): Long {
        return dao.insertReminder(reminder)
    }

    suspend fun addChatMessage(message: AIChatMessageEntity): Long {
        return dao.insertChatMessage(message)
    }

    suspend fun clearChat() {
        dao.clearChat()
    }

    suspend fun getAllHabits(): List<HabitEntity> = dao.getAllHabits()

    suspend fun getLogsForHabit(habitId: Long): List<HabitLogEntity> = dao.getLogsForHabit(habitId)

    suspend fun getRecentCheckIns(): List<DailyCheckInEntity> = dao.getRecentCheckIns()

    suspend fun calculateHabitStreak(habitId: Long): Int {
        val logs = dao.getLogsForHabit(habitId).map { it.date }.toSet()
        if (logs.isEmpty()) return 0

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        var streak = 0

        // Check if completed today, or start from yesterday if not yet today
        val todayStr = sdf.format(cal.time)
        val completedToday = logs.contains(todayStr)
        if (completedToday) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            // Check yesterday
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)
            if (!logs.contains(yesterdayStr)) {
                return 0
            }
        }

        while (true) {
            val dateStr = sdf.format(cal.time)
            if (logs.contains(dateStr)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }
}
