package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.ai.AICoachEngine
import com.example.data.ai.HabitProgressSnapshot
import com.example.data.db.HabitDatabase
import com.example.data.model.AIChatMessageEntity
import com.example.data.model.DailyCheckInEntity
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.MotivationalReminderEntity
import com.example.data.repository.HabitRepository
import com.example.util.DateUtils
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MotivationalQuote(
    val quote: String,
    val author: String,
    val category: String
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HabitRepository
    private val aiCoachEngine = AICoachEngine()

    val todayDateString: String = DateUtils.getTodayString()
    private val currentMonthPrefix: String = todayDateString.substring(0, 7) // "yyyy-MM"

    val habits: StateFlow<List<HabitEntity>>
    val todayLogs: StateFlow<List<HabitLogEntity>>
    val monthLogs: StateFlow<List<HabitLogEntity>>
    val todayCheckIn: StateFlow<DailyCheckInEntity?>
    val monthCheckIns: StateFlow<List<DailyCheckInEntity>>
    val reminders: StateFlow<List<MotivationalReminderEntity>>
    val chatMessages: StateFlow<List<AIChatMessageEntity>>

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _aiInsights = MutableStateFlow<List<String>>(emptyList())
    val aiInsights: StateFlow<List<String>> = _aiInsights.asStateFlow()

    private val _currentQuote = MutableStateFlow(
        MotivationalQuote(
            quote = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            author = "Will Durant",
            category = "Consistency"
        )
    )
    val currentQuote: StateFlow<MotivationalQuote> = _currentQuote.asStateFlow()

    private val quotesPool = listOf(
        MotivationalQuote("Small disciplines repeated with consistency every day lead to great achievements.", "John C. Maxwell", "Consistency"),
        MotivationalQuote("You do not rise to the level of your goals. You fall to the level of your systems.", "James Clear", "Atomic Habits"),
        MotivationalQuote("Motivation is what gets you started. Habit is what keeps you going.", "Jim Ryun", "Mindset"),
        MotivationalQuote("It does not matter how slowly you go as long as you do not stop.", "Confucius", "Patience"),
        MotivationalQuote("Energy flows where attention goes. Direct it toward your daily rituals.", "Tony Robbins", "Focus"),
        MotivationalQuote("The secret of your future is hidden in your daily routine.", "Mike Murdock", "Routines")
    )

    init {
        val dao = HabitDatabase.getInstance(application).habitDao()
        repository = HabitRepository(dao)

        habits = repository.allHabitsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        todayLogs = repository.getLogsForDateFlow(todayDateString).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        monthLogs = repository.getLogsForMonthFlow(currentMonthPrefix).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        todayCheckIn = repository.getCheckInForDateFlow(todayDateString).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        monthCheckIns = repository.getCheckInsForMonthFlow(currentMonthPrefix).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        reminders = repository.remindersFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        chatMessages = repository.chatMessagesFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        refreshInsights()
    }

    fun toggleHabit(habitId: Long, date: String = todayDateString) {
        viewModelScope.launch {
            val logs = if (date == todayDateString) todayLogs.value else monthLogs.value
            val isCompleted = logs.any { it.habitId == habitId && it.date == date && it.isCompleted }
            repository.toggleHabitCompletion(habitId, date, isCompleted)
            NotificationHelper.triggerHapticFeedback(getApplication())
            refreshInsights()
        }
    }

    fun clearAllTicks() {
        viewModelScope.launch {
            repository.clearAllTicks()
            NotificationHelper.triggerHapticFeedback(getApplication())
            refreshInsights()
        }
    }

    fun addNewHabit(
        title: String,
        category: String,
        timeOfDay: String,
        iconName: String,
        targetGoal: Int,
        colorHex: String
    ) {
        viewModelScope.launch {
            val newHabit = HabitEntity(
                title = title.trim(),
                category = category,
                timeOfDay = timeOfDay,
                iconName = iconName,
                targetGoal = targetGoal,
                colorHex = colorHex,
                createdDate = todayDateString
            )
            repository.addHabit(newHabit)
            refreshInsights()
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            refreshInsights()
        }
    }

    fun saveCheckIn(mood: String, sleepHours: Float, weather: String, reflection: String) {
        viewModelScope.launch {
            repository.saveCheckIn(
                DailyCheckInEntity(
                    date = todayDateString,
                    mood = mood,
                    sleepHours = sleepHours,
                    weather = weather,
                    reflectionNote = reflection
                )
            )
            refreshInsights()
        }
    }

    fun toggleReminder(reminder: MotivationalReminderEntity) {
        viewModelScope.launch {
            repository.updateReminder(reminder.copy(isEnabled = !reminder.isEnabled))
        }
    }

    fun sendTestNotification(reminder: MotivationalReminderEntity) {
        NotificationHelper.showMotivationalNotification(
            getApplication(),
            reminder.id.toInt(),
            reminder.title,
            reminder.message
        )
    }

    fun refreshQuote() {
        val current = _currentQuote.value
        val next = quotesPool.filter { it.quote != current.quote }.randomOrNull() ?: quotesPool.first()
        _currentQuote.value = next
    }

    fun sendMessageToAI(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            repository.addChatMessage(
                AIChatMessageEntity(
                    role = "user",
                    content = userText.trim(),
                    timestamp = System.currentTimeMillis()
                )
            )

            _isAiThinking.value = true
            val snapshot = buildSnapshot()

            val apiKey = try {
                val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
                field.get(null) as? String
            } catch (e: Exception) {
                null
            }

            val reply = aiCoachEngine.askAssistant(userText, snapshot, apiKey)

            repository.addChatMessage(
                AIChatMessageEntity(
                    role = "assistant",
                    content = reply,
                    timestamp = System.currentTimeMillis()
                )
            )
            _isAiThinking.value = false
        }
    }

    fun refreshInsights() {
        viewModelScope.launch {
            val snapshot = buildSnapshot()
            val insights = aiCoachEngine.generatePersonalizedInsights(snapshot)
            _aiInsights.value = insights
        }
    }

    private suspend fun buildSnapshot(): HabitProgressSnapshot {
        val allHabitsList = repository.getAllHabits()
        val dailyHabits = allHabitsList.filter { it.category == "daily" }
        val weeklyHabits = allHabitsList.filter { it.category == "weekly" }
        val monthlyHabits = allHabitsList.filter { it.category == "monthly" }

        val todayLogsList = repository.getLogsForDate(todayDateString)
        val completedDailyToday = todayLogsList.count { log -> dailyHabits.any { it.id == log.habitId } }

        val checkIns = repository.getRecentCheckIns()
        val avgSleep = if (checkIns.isNotEmpty()) checkIns.map { it.sleepHours }.average().toFloat() else 7.2f
        val mostFrequentMood = checkIns.groupBy { it.mood }.maxByOrNull { it.value.size }?.key ?: "Happy"

        val dailyRate = if (dailyHabits.isNotEmpty()) {
            (completedDailyToday.toFloat() / dailyHabits.size.toFloat()).coerceIn(0f, 1f)
        } else 0f

        val topHabit = dailyHabits.firstOrNull()?.title ?: "Hydration & Sunlight"
        val lowestHabit = dailyHabits.lastOrNull()?.title ?: "Evening Wind-Down"

        return HabitProgressSnapshot(
            totalDailyHabits = dailyHabits.size,
            completedDailyToday = completedDailyToday,
            dailyCompletionRate = dailyRate,
            weeklyCompletionRate = 0.72f,
            monthlyCompletionRate = 0.64f,
            bestStreak = 13,
            topHabitTitle = topHabit,
            lowestHabitTitle = lowestHabit,
            averageSleepHours = avgSleep,
            mostFrequentMood = mostFrequentMood,
            habitsSummaryList = dailyHabits.map { it.title }
        )
    }
}
