package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HabitViewModel
import com.example.ui.components.HabitBloomPlantView
import com.example.ui.components.HabitCard
import com.example.ui.components.MonthlyHighlightsCard
import com.example.ui.components.TopKpiRow
import com.example.ui.theme.LightBluePillBg
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfaceOffWhite
import com.example.ui.theme.SurfacePureWhite
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.WhiteBackground
import com.example.util.DateUtils

@Composable
fun TodayScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.habits.collectAsState()
    val todayLogs by viewModel.todayLogs.collectAsState()
    val todayCheckIn by viewModel.todayCheckIn.collectAsState()
    val quote by viewModel.currentQuote.collectAsState()

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showCheckInDialog by remember { mutableStateOf(false) }
    var showClearConfirmationDialog by remember { mutableStateOf(false) }

    val dailyHabits = habits.filter { it.category == "daily" }
    val completedDailyToday = todayLogs.count { log -> dailyHabits.any { it.id == log.habitId } }
    val bloomProgress = if (dailyHabits.isNotEmpty()) {
        completedDailyToday.toFloat() / dailyHabits.size.toFloat()
    } else 0f

    val completedKeys = remember(todayLogs) {
        todayLogs.filter { it.isCompleted }.map { it.habitId }.toSet()
    }

    val morningHabits = habits.filter { it.timeOfDay == "morning" }
    val afternoonHabits = habits.filter { it.timeOfDay == "afternoon" || it.timeOfDay == "anytime" }
    val eveningHabits = habits.filter { it.timeOfDay == "evening" || it.timeOfDay == "night" }

    Scaffold(
        containerColor = WhiteBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddHabitDialog = true },
                containerColor = SkyBluePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_habit")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // App Header with Creator Name: "Made by Riddham "
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "HABITS BOOK",
                                style = MaterialTheme.typography.titleLarge,
                                color = SkyBluePrimary,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Maker badge: "Made by Riddham "
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LightBluePillBg)
                                    .border(1.dp, SkyBlueAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Maker",
                                        tint = SkyBluePrimary,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Made by Riddham",
                                        color = SkyBluePrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = DateUtils.getTodayFormatted(),
                            color = TextDarkSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Small steps, every day.",
                            color = TextDarkMuted,
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }

                    // Check-in status pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfacePureWhite)
                            .border(1.dp, SurfaceBorderLight, RoundedCornerShape(12.dp))
                            .clickable { showCheckInDialog = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("button_open_checkin")
                    ) {
                        Text(
                            text = if (todayCheckIn != null) {
                                val emoji = when (todayCheckIn?.mood?.lowercase()) {
                                    "happy" -> "😊"
                                    "loved" -> "🥰"
                                    "calm" -> "😌"
                                    "okay" -> "😐"
                                    "tired" -> "🥱"
                                    "sad" -> "😢"
                                    "stressed" -> "😫"
                                    else -> "✨"
                                }
                                "$emoji ${todayCheckIn?.sleepHours}h"
                            } else {
                                "Log Mood ✍️"
                            },
                            color = SkyBluePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Quick Actions: Clear All Ticks bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEW USER READY: CLEAN SLATE",
                        color = TextDarkMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceOffWhite)
                            .border(1.dp, SurfaceBorderLight, RoundedCornerShape(8.dp))
                            .clickable { showClearConfirmationDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("button_clear_all_ticks")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = "Clear",
                                tint = SkyBluePrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Clear All Ticks",
                                color = SkyBluePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Motivational Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfacePureWhite)
                        .border(1.dp, SurfaceBorderLight, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Quote",
                            tint = SkyBlueAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "\"${quote.quote}\"",
                                color = TextDarkPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— ${quote.author} (${quote.category})",
                                color = TextDarkMuted,
                                fontSize = 11.sp
                            )
                        }
                        IconButton(
                            onClick = { viewModel.refreshQuote() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Quote",
                                tint = TextDarkSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Top KPI rings (Daily %, Weekly %, Monthly %)
            item {
                TopKpiRow(
                    dailyProgress = bloomProgress,
                    dailyCompletedText = "$completedDailyToday of ${dailyHabits.size} ticks",
                    weeklyProgress = 0f,
                    weeklyCompletedText = "0 of 5 tasks",
                    monthlyProgress = 0f,
                    monthlyCompletedText = "0 of 4 goals"
                )
            }

            // Today's Bloom Gamified Plant Growth Card
            item {
                HabitBloomPlantView(bloomProgress = bloomProgress)
            }

            // Routine Section: Morning
            if (morningHabits.isNotEmpty()) {
                item {
                    RoutineSectionHeader(title = "MORNING ROUTINE", subtitle = "Sunlight, hydration & early focus")
                }
                items(morningHabits, key = { it.id }) { habit ->
                    HabitCard(
                        habit = habit,
                        isCompleted = completedKeys.contains(habit.id),
                        streakCount = if (completedKeys.contains(habit.id)) 1 else 0,
                        onToggle = { viewModel.toggleHabit(habit.id) },
                        onDelete = { viewModel.deleteHabit(habit) }
                    )
                }
            }

            // Routine Section: Afternoon & Focus
            if (afternoonHabits.isNotEmpty()) {
                item {
                    RoutineSectionHeader(title = "MIDDAY & HEALTH", subtitle = "Movement, nutrition & productivity")
                }
                items(afternoonHabits, key = { it.id }) { habit ->
                    HabitCard(
                        habit = habit,
                        isCompleted = completedKeys.contains(habit.id),
                        streakCount = if (completedKeys.contains(habit.id)) 1 else 0,
                        onToggle = { viewModel.toggleHabit(habit.id) },
                        onDelete = { viewModel.deleteHabit(habit) }
                    )
                }
            }

            // Routine Section: Evening
            if (eveningHabits.isNotEmpty()) {
                item {
                    RoutineSectionHeader(title = "EVENING WIND-DOWN", subtitle = "Mindfulness, family & restorative rest")
                }
                items(eveningHabits, key = { it.id }) { habit ->
                    HabitCard(
                        habit = habit,
                        isCompleted = completedKeys.contains(habit.id),
                        streakCount = if (completedKeys.contains(habit.id)) 1 else 0,
                        onToggle = { viewModel.toggleHabit(habit.id) },
                        onDelete = { viewModel.deleteHabit(habit) }
                    )
                }
            }

            // Month At A Glance
            item {
                MonthlyHighlightsCard(
                    bestStreakDays = if (completedDailyToday > 0) 1 else 0,
                    perfectDaysCount = if (completedDailyToday == dailyHabits.size && dailyHabits.isNotEmpty()) 1 else 0,
                    avgSleepHours = todayCheckIn?.sleepHours ?: 7.5f,
                    dominantMood = todayCheckIn?.mood ?: "Happy"
                )
            }

            // Footer with Maker Branding
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Habits Book • Made by Riddham",
                        color = TextDarkMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showClearConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmationDialog = false },
            title = { Text("Clear All Ticks?", color = TextDarkPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will reset all checked habits to an unticked state so you can log your activities fresh.", color = TextDarkSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllTicks()
                        showClearConfirmationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                ) {
                    Text("Clear Ticks")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirmationDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = SurfacePureWhite
        )
    }

    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onSave = { title, category, timeOfDay, iconName, targetGoal ->
                viewModel.addNewHabit(
                    title = title,
                    category = category,
                    timeOfDay = timeOfDay,
                    iconName = iconName,
                    targetGoal = targetGoal,
                    colorHex = "#0284C7"
                )
            }
        )
    }

    if (showCheckInDialog) {
        DailyCheckInDialog(
            initialMood = todayCheckIn?.mood ?: "Happy",
            initialSleep = todayCheckIn?.sleepHours ?: 7.5f,
            initialWeather = todayCheckIn?.weather ?: "Sunny",
            initialReflection = todayCheckIn?.reflectionNote ?: "",
            onDismiss = { showCheckInDialog = false },
            onSave = { mood, sleep, weather, note ->
                viewModel.saveCheckIn(mood, sleep, weather, note)
            }
        )
    }
}

@Composable
fun RoutineSectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = SkyBluePrimary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Text(
            text = subtitle,
            color = TextDarkMuted,
            fontSize = 11.sp
        )
    }
}
