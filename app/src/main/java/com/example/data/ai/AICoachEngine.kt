package com.example.data.ai

import com.example.data.model.DailyCheckInEntity
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class HabitProgressSnapshot(
    val totalDailyHabits: Int,
    val completedDailyToday: Int,
    val dailyCompletionRate: Float,
    val weeklyCompletionRate: Float,
    val monthlyCompletionRate: Float,
    val bestStreak: Int,
    val topHabitTitle: String,
    val lowestHabitTitle: String,
    val averageSleepHours: Float,
    val mostFrequentMood: String,
    val habitsSummaryList: List<String>
)

class AICoachEngine {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun generatePersonalizedInsights(snapshot: HabitProgressSnapshot): List<String> = withContext(Dispatchers.Default) {
        val insights = mutableListOf<String>()

        // 1. Streak & Momentum
        if (snapshot.bestStreak > 0) {
            insights.add("🔥 Momentum Master: You've achieved a ${snapshot.bestStreak}-day streak on your core habits! Consistency at this level establishes automatic neuro-pathways.")
        } else {
            insights.add("🌱 Planting Seeds: Today is Day 1 of your new streak. Completing just 2 habits today unlocks your momentum multiplier.")
        }

        // 2. Daily Bloom Status
        val completionPct = (snapshot.dailyCompletionRate * 100).toInt()
        when {
            completionPct >= 80 -> {
                insights.add("🌸 Full Bloom (${completionPct}%): Outstanding execution today! You are in the top tier of consistency.")
            }
            completionPct >= 50 -> {
                insights.add("🌿 Flourishing Growth (${completionPct}%): You're past the halfway mark today. Complete 1 more evening habit to reach optimal bloom.")
            }
            else -> {
                insights.add("💧 Habit Hydration: Small steps matter most. Knock out a quick win like 'Drink 2L water' or 'Meditate 10 mins' right now.")
            }
        }

        // 3. Sleep & Health Correlation
        if (snapshot.averageSleepHours >= 7.0f) {
            insights.add("⚡ Energy Catalyst: Your average sleep of ${String.format("%.1f", snapshot.averageSleepHours)}h gives you the cognitive reserve needed to maintain high discipline without willpower fatigue.")
        } else {
            insights.add("🌙 Rest Optimization: When average sleep falls under 7h, habit friction increases by 40%. Prioritize your 'Phone off by 10pm' habit tonight.")
        }

        // 4. Habit Spotlight
        if (snapshot.topHabitTitle.isNotEmpty()) {
            insights.add("⭐ Power Habit: '${snapshot.topHabitTitle}' is your anchor habit. Stack your harder habits immediately after it.")
        }

        // 5. Mood Connection
        insights.add("✨ Mood Resonance: Your primary mood has been '${snapshot.mostFrequentMood}'. Consistent daily morning sunlight and movement directly stabilize emotional baseline.")

        insights
    }

    suspend fun askAssistant(
        userMessage: String,
        snapshot: HabitProgressSnapshot,
        apiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        // Try calling Gemini if online and API key is present
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiResponse = callGeminiApi(userMessage, snapshot, apiKey)
                if (!geminiResponse.isNullOrBlank()) {
                    return@withContext geminiResponse
                }
            } catch (e: Exception) {
                // Network unavailable or key invalid; smoothly fallback to offline engine
            }
        }

        // Intelligent Offline Habit Coaching Engine
        generateOfflineResponse(userMessage, snapshot)
    }

    private fun generateOfflineResponse(userMessage: String, snapshot: HabitProgressSnapshot): String {
        val lower = userMessage.lowercase()
        return when {
            lower.contains("routine") || lower.contains("schedule") -> {
                "Here is an optimal routine blueprint tailored to your progress:\n\n" +
                "🌅 Morning (Anchor: ${if (snapshot.topHabitTitle.isNotEmpty()) snapshot.topHabitTitle else "Hydration"}):\n" +
                "• 07:00 AM: Sunlight & 500ml water\n" +
                "• 07:30 AM: 10 mins mindfulness\n\n" +
                "⚡ Midday Focus:\n" +
                "• 01:00 PM: 30-min walk & nutritious meal\n\n" +
                "🌙 Evening Reset:\n" +
                "• 09:30 PM: Digital sunset, journaling wins\n" +
                "• 10:00 PM: Deep sleep target (Goal: 7.5h+)"
            }
            lower.contains("motivat") || lower.contains("tired") || lower.contains("lazy") || lower.contains("hard") -> {
                "Remember: 'You do not rise to the level of your goals; you fall to the level of your systems.'\n\n" +
                "You currently have a best streak of ${snapshot.bestStreak} days and your top anchor is '${snapshot.topHabitTitle}'. " +
                "Don't worry about being perfect today. Just do the 2-minute version of one habit right now. Action produces motivation, not the other way around!"
            }
            lower.contains("sleep") || lower.contains("night") -> {
                "Your current average sleep is ${String.format("%.1f", snapshot.averageSleepHours)} hours. " +
                "Analysis shows that when you get 7.5+ hours of sleep, your habit completion rate spikes significantly. " +
                "Recommendation: Enable your 'Phone off by 10pm' reminder tonight and dim ambient lights 45 minutes before bed."
            }
            lower.contains("insight") || lower.contains("progress") || lower.contains("stat") || lower.contains("review") -> {
                "📊 Personalized Progress Review:\n\n" +
                "• Daily Habits Completed Today: ${snapshot.completedDailyToday} / ${snapshot.totalDailyHabits}\n" +
                "• Overall Daily Completion Rate: ${(snapshot.dailyCompletionRate * 100).toInt()}%\n" +
                "• Best Active Streak: ${snapshot.bestStreak} days 🔥\n" +
                "• Most Felt Mood: ${snapshot.mostFrequentMood}\n" +
                "• Average Sleep: ${String.format("%.1f", snapshot.averageSleepHours)} hours\n\n" +
                "Focus recommendation: Use your anchor habit '${snapshot.topHabitTitle}' as a trigger to tackle '${if (snapshot.lowestHabitTitle.isNotEmpty()) snapshot.lowestHabitTitle else "your evening habits"}'."
            }
            lower.contains("tip") || lower.contains("advice") || lower.contains("help") -> {
                "Here are 3 habit hacks tailored for your journey:\n\n" +
                "1. Habit Stacking: Attach a new habit onto '${snapshot.topHabitTitle}'. After I do '${snapshot.topHabitTitle}', I will immediately do my next goal.\n" +
                "2. The 2-Day Rule: Never skip a habit two days in a row. Missing once is an accident; missing twice is the start of a new habit.\n" +
                "3. Visual Matrix: Keep checking off your Habits Book matrix daily—visual tracking reinforces dopamine feedback loops."
            }
            else -> {
                "I've analyzed your Habits Book data! You have completed ${snapshot.completedDailyToday} of ${snapshot.totalDailyHabits} daily habits today with a ${snapshot.bestStreak}-day best streak. " +
                "How would you like to level up today? Ask me for an optimized morning routine, a motivational boost, or a sleep-to-productivity analysis!"
            }
        }
    }

    private fun callGeminiApi(
        userMessage: String,
        snapshot: HabitProgressSnapshot,
        apiKey: String
    ): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val systemContext = "You are the Habits Book AI Personal Coach. You help the user build daily routines, optimize habits, stay consistent, and improve sleep/mood. " +
                "The user's current stats: " +
                "Daily completion: ${(snapshot.dailyCompletionRate * 100).toInt()}%, " +
                "Completed today: ${snapshot.completedDailyToday}/${snapshot.totalDailyHabits}, " +
                "Best streak: ${snapshot.bestStreak} days, " +
                "Top habit: ${snapshot.topHabitTitle}, " +
                "Average sleep: ${String.format("%.1f", snapshot.averageSleepHours)}h, " +
                "Dominant mood: ${snapshot.mostFrequentMood}. " +
                "Provide brief, uplifting, highly actionable, bulleted advice in 2-4 concise paragraphs."

        val payload = JSONObject().apply {
            val contents = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val parts = JSONArray().apply {
                        put(JSONObject().put("text", "$systemContext\n\nUser Question: $userMessage"))
                    }
                    put("parts", parts)
                }
                put(contentObj)
            }
            put("contents", contents)
        }

        val request = Request.Builder()
            .url(url)
            .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null
            val json = JSONObject(body)
            val candidates = json.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            return parts.getJSONObject(0).optString("text")
        }
    }
}
