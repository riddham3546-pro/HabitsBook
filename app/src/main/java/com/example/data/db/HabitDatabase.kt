package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AIChatMessageEntity
import com.example.data.model.DailyCheckInEntity
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.data.model.MotivationalReminderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

@Database(
    entities = [
        HabitEntity::class,
        HabitLogEntity::class,
        DailyCheckInEntity::class,
        MotivationalReminderEntity::class,
        AIChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getInstance(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_bloom.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            populateInitialData(getInstance(context).habitDao())
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(dao: HabitDao) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayCal = Calendar.getInstance()
            val todayStr = sdf.format(todayCal.time)

            // Initial Daily Habits inspired by the user's screenshot
            val initialHabits = listOf(
                HabitEntity(title = "Sleep 8+ hours", category = "daily", timeOfDay = "night", iconName = "bed", targetGoal = 30, colorHex = "#38BDF8", createdDate = todayStr),
                HabitEntity(title = "Drink 2L of water", category = "daily", timeOfDay = "morning", iconName = "water", targetGoal = 31, colorHex = "#00E5FF", createdDate = todayStr),
                HabitEntity(title = "Morning sunlight", category = "daily", timeOfDay = "morning", iconName = "sun", targetGoal = 25, colorHex = "#F59E0B", createdDate = todayStr),
                HabitEntity(title = "No added sugar", category = "daily", timeOfDay = "anytime", iconName = "restaurant", targetGoal = 31, colorHex = "#F43F5E", createdDate = todayStr),
                HabitEntity(title = "Read 20 pages", category = "daily", timeOfDay = "afternoon", iconName = "book", targetGoal = 25, colorHex = "#A855F7", createdDate = todayStr),
                HabitEntity(title = "Move for 30 mins", category = "daily", timeOfDay = "afternoon", iconName = "run", targetGoal = 20, colorHex = "#10B981", createdDate = todayStr),
                HabitEntity(title = "Eat my greens", category = "daily", timeOfDay = "afternoon", iconName = "spa", targetGoal = 31, colorHex = "#10B981", createdDate = todayStr),
                HabitEntity(title = "Meditate 10 mins", category = "daily", timeOfDay = "evening", iconName = "meditate", targetGoal = 25, colorHex = "#7DD3FC", createdDate = todayStr),
                HabitEntity(title = "Phone off by 10pm", category = "daily", timeOfDay = "night", iconName = "phone", targetGoal = 31, colorHex = "#6366F1", createdDate = todayStr),
                HabitEntity(title = "Journal", category = "daily", timeOfDay = "evening", iconName = "book", targetGoal = 25, colorHex = "#EC4899", createdDate = todayStr),
                HabitEntity(title = "30min night walk", category = "daily", timeOfDay = "night", iconName = "run", targetGoal = 15, colorHex = "#06B6D4", createdDate = todayStr),
                HabitEntity(title = "Time with family", category = "daily", timeOfDay = "evening", iconName = "family", targetGoal = 10, colorHex = "#F59E0B", createdDate = todayStr),
                HabitEntity(title = "No coffee till 12pm", category = "daily", timeOfDay = "morning", iconName = "coffee", targetGoal = 20, colorHex = "#D97706", createdDate = todayStr),

                // Weekly Habits
                HabitEntity(title = "Meal prep Sunday", category = "weekly", timeOfDay = "anytime", iconName = "restaurant", targetGoal = 4, colorHex = "#F43F5E", createdDate = todayStr),
                HabitEntity(title = "Deep clean kitchen", category = "weekly", timeOfDay = "anytime", iconName = "clean", targetGoal = 4, colorHex = "#38BDF8", createdDate = todayStr),
                HabitEntity(title = "Call grandma", category = "weekly", timeOfDay = "anytime", iconName = "family", targetGoal = 4, colorHex = "#EC4899", createdDate = todayStr),
                HabitEntity(title = "Yoga class", category = "weekly", timeOfDay = "morning", iconName = "meditate", targetGoal = 4, colorHex = "#10B981", createdDate = todayStr),
                HabitEntity(title = "Digital detox day", category = "weekly", timeOfDay = "anytime", iconName = "phone", targetGoal = 4, colorHex = "#8B5CF6", createdDate = todayStr),

                // Monthly Habits
                HabitEntity(title = "Medical check-up", category = "monthly", timeOfDay = "anytime", iconName = "spa", targetGoal = 1, colorHex = "#38BDF8", createdDate = todayStr),
                HabitEntity(title = "Plan next month's goals", category = "monthly", timeOfDay = "anytime", iconName = "book", targetGoal = 1, colorHex = "#F59E0B", createdDate = todayStr),
                HabitEntity(title = "Long nature hike", category = "monthly", timeOfDay = "anytime", iconName = "run", targetGoal = 2, colorHex = "#10B981", createdDate = todayStr),
                HabitEntity(title = "Review subscriptions", category = "monthly", timeOfDay = "anytime", iconName = "clean", targetGoal = 1, colorHex = "#A855F7", createdDate = todayStr)
            )
            dao.insertHabits(initialHabits)

            // Seed logs for previous days of current month so charts & matrix are populated immediately
            val allInserted = dao.getAllHabits()
            val cal = Calendar.getInstance()
            val currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
            val currentYear = cal.get(Calendar.YEAR)
            val currentMonth = cal.get(Calendar.MONTH)

            val moods = listOf("Happy", "Loved", "Calm", "Okay", "Happy", "Calm", "Tired")
            val weathers = listOf("Sunny", "Partly cloudy", "Sunny", "Cloudy", "Rainy")

            for (day in 1..currentDayOfMonth) {
                cal.set(currentYear, currentMonth, day)
                val dateString = sdf.format(cal.time)

                // Log check-ins
                val mood = moods[day % moods.size]
                val weather = weathers[day % weathers.size]
                val sleep = 6.5f + (day % 4) * 0.5f
                dao.insertOrUpdateCheckIn(
                    DailyCheckInEntity(
                        date = dateString,
                        mood = mood,
                        sleepHours = sleep,
                        weather = weather,
                        reflectionNote = if (day % 3 == 0) "Stayed consistent with morning sunlight and hydration!" else ""
                    )
                )
            }

            // No pre-ticked habit logs seeded: new users tick habits themselves!

            // Initial Motivational Reminders
            val initialReminders = listOf(
                MotivationalReminderEntity(
                    title = "Rise & Shine Kickstarter",
                    message = "Small steps every day build an empire. Drink 2L water & step into the morning sunlight!",
                    scheduledTime = "08:00 AM",
                    period = "Morning",
                    isEnabled = true
                ),
                MotivationalReminderEntity(
                    title = "Midday Momentum Boost",
                    message = "You're halfway through! Move for 30 minutes, nourish your body, and stay locked in.",
                    scheduledTime = "01:00 PM",
                    period = "Midday",
                    isEnabled = true
                ),
                MotivationalReminderEntity(
                    title = "Evening Decompression",
                    message = "Take 10 mindful minutes to breathe and meditate. Acknowledge what you accomplished today.",
                    scheduledTime = "06:30 PM",
                    period = "Evening",
                    isEnabled = true
                ),
                MotivationalReminderEntity(
                    title = "Digital Sunset & Deep Sleep",
                    message = "Power down your screens. Great tomorrow starts with 8 hours of restorative rest tonight.",
                    scheduledTime = "09:30 PM",
                    period = "Night",
                    isEnabled = true
                )
            )
            dao.insertReminders(initialReminders)

            // Initial AI Assistant greeting
            dao.insertChatMessage(
                AIChatMessageEntity(
                    role = "assistant",
                    content = "Hello! I am your AI Habit & Routine Coach. I analyze your daily streaks, sleep correlations, and habit consistency to provide tailored insights. Ask me anything or request a personalized weekly routine optimization!",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
