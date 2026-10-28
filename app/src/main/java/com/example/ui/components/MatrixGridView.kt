package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyCheckInEntity
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.ui.theme.HabitBloomGreen
import com.example.ui.theme.LightBluePillBg
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfaceOffWhite
import com.example.ui.theme.SurfacePureWhite
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.util.CalendarDay
import com.example.util.DateUtils
import java.util.Calendar

private val HEADER_HEIGHT = 40.dp
private val ROW_HEIGHT = 36.dp
private val INFO_ROW_HEIGHT = 28.dp

@Composable
fun MatrixGridView(
    habits: List<HabitEntity>,
    monthLogs: List<HabitLogEntity>,
    monthCheckIns: List<DailyCheckInEntity>,
    todayDateStr: String,
    onToggleCell: (habitId: Long, dateStr: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()

    val currentCal = remember { Calendar.getInstance() }
    val daysInMonth = remember {
        DateUtils.getDaysInMonth(
            currentCal.get(Calendar.YEAR),
            currentCal.get(Calendar.MONTH)
        )
    }

    // Set of completed keys: "habitId_dateStr"
    val completedKeys = remember(monthLogs) {
        monthLogs.filter { it.isCompleted }.map { "${it.habitId}_${it.date}" }.toSet()
    }

    // CheckIn by date
    val checkInMap = remember(monthCheckIns) {
        monthCheckIns.associateBy { it.date }
    }

    // Daily completions count for top bar chart
    val dailyCompletionsCount = remember(monthLogs, daysInMonth) {
        daysInMonth.associate { day ->
            val count = monthLogs.count { it.date == day.dateString && it.isCompleted }
            day.dayNumber to count
        }
    }
    val maxCompletions = (dailyCompletionsCount.values.maxOrNull() ?: 5).coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("matrix_grid_view")
    ) {
        // Daily Activity Bar Chart
        DailyActivityBarChart(
            daysInMonth = daysInMonth,
            completionsCount = dailyCompletionsCount,
            maxCompletions = maxCompletions,
            todayDateStr = todayDateStr
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Matrix Card with Pinned Left Column (Habit Names remain visible while scrolling days)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfacePureWhite)
                .border(1.dp, SurfaceBorderLight, RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // PINNED / FROZEN LEFT COLUMN: Habit Titles stay completely visible!
                Column(
                    modifier = Modifier
                        .width(132.dp)
                        .background(SurfacePureWhite)
                        .padding(start = 10.dp, end = 6.dp)
                ) {
                    // Header for Habit Name
                    Box(
                        modifier = Modifier
                            .height(HEADER_HEIGHT)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "HABIT NAME",
                            color = SkyBluePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Habit title items
                    habits.forEach { habit ->
                        Box(
                            modifier = Modifier
                                .height(ROW_HEIGHT)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = getHabitIcon(habit.iconName),
                                    contentDescription = habit.title,
                                    tint = SkyBluePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = habit.title,
                                    color = TextDarkPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mood label
                    Box(
                        modifier = Modifier
                            .height(INFO_ROW_HEIGHT)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "MOOD",
                            color = TextDarkMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Sleep label
                    Box(
                        modifier = Modifier
                            .height(INFO_ROW_HEIGHT)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "SLEEP (hrs)",
                            color = TextDarkMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Vertical Divider between pinned column and scrollable content
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(HEADER_HEIGHT + (ROW_HEIGHT * habits.size) + (INFO_ROW_HEIGHT * 2) + 8.dp)
                        .background(SurfaceBorderLight)
                )

                // SCROLLABLE RIGHT CONTENT (Days 1..31 + Summary Columns)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(horizontalScrollState)
                        .padding(start = 6.dp, end = 10.dp)
                ) {
                    // Header Row: Days 1..31 and Summary columns
                    Row(
                        modifier = Modifier.height(HEADER_HEIGHT),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        daysInMonth.forEach { day ->
                            val isToday = day.dateString == todayDateStr
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(28.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isToday) LightBluePillBg else Color.Transparent)
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = day.dayOfWeek,
                                    color = if (isToday) SkyBluePrimary else TextDarkMuted,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${day.dayNumber}",
                                    color = if (isToday) SkyBluePrimary else TextDarkPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "GOAL", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp), textAlign = TextAlign.Center)
                        Text(text = "DONE", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp), textAlign = TextAlign.Center)
                        Text(text = "%", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(42.dp), textAlign = TextAlign.Center)
                        Text(text = "STREAK", color = TextDarkMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(52.dp), textAlign = TextAlign.Center)
                    }

                    // Habit Rows (Checkboxes for Days 1..31)
                    habits.forEach { habit ->
                        val doneCount = daysInMonth.count { completedKeys.contains("${habit.id}_${it.dateString}") }
                        val pct = if (habit.targetGoal > 0) ((doneCount.toFloat() / habit.targetGoal.toFloat()) * 100).toInt() else 0

                        Row(
                            modifier = Modifier.height(ROW_HEIGHT),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            daysInMonth.forEach { day ->
                                val isChecked = completedKeys.contains("${habit.id}_${day.dateString}")
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .padding(2.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            if (isChecked) SkyBlueAccent else SurfacePureWhite
                                        )
                                        .border(
                                            1.dp,
                                            if (isChecked) SkyBluePrimary else Color(0xFFCBD5E1),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable { onToggleCell(habit.id, day.dateString) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Done",
                                            tint = Color.White,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${habit.targetGoal}", color = TextDarkSecondary, fontSize = 11.sp, modifier = Modifier.width(38.dp), textAlign = TextAlign.Center)
                            Text(text = "$doneCount", color = SkyBluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp), textAlign = TextAlign.Center)
                            Text(text = "$pct%", color = if (pct >= 80) HabitBloomGreen else TextDarkSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(42.dp), textAlign = TextAlign.Center)
                            Row(
                                modifier = Modifier.width(52.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = StreakFlame, modifier = Modifier.size(12.dp))
                                Text(text = "${doneCount}", color = StreakFlame, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mood Row Emojis
                    Row(
                        modifier = Modifier.height(INFO_ROW_HEIGHT),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        daysInMonth.forEach { day ->
                            val checkIn = checkInMap[day.dateString]
                            val moodEmoji = when (checkIn?.mood?.lowercase()) {
                                "happy" -> "😊"
                                "loved" -> "🥰"
                                "calm" -> "😌"
                                "okay" -> "😐"
                                "tired" -> "🥱"
                                "sad" -> "😢"
                                "stressed" -> "😫"
                                else -> "·"
                            }
                            Box(
                                modifier = Modifier.size(28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = moodEmoji, fontSize = 11.sp)
                            }
                        }
                    }

                    // Sleep Row Numbers
                    Row(
                        modifier = Modifier.height(INFO_ROW_HEIGHT),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        daysInMonth.forEach { day ->
                            val checkIn = checkInMap[day.dateString]
                            val sleepText = if (checkIn != null) String.format("%.0f", checkIn.sleepHours) else "·"
                            Box(
                                modifier = Modifier.size(28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sleepText,
                                    color = SkyBluePrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyActivityBarChart(
    daysInMonth: List<CalendarDay>,
    completionsCount: Map<Int, Int>,
    maxCompletions: Int,
    todayDateStr: String
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfacePureWhite)
            .border(1.dp, SurfaceBorderLight, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "DAILY ACTIVITY TREND",
                    style = MaterialTheme.typography.labelSmall,
                    color = SkyBluePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Habits Completed / Day",
                    color = TextDarkMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .height(76.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                daysInMonth.forEach { day ->
                    val count = completionsCount[day.dayNumber] ?: 0
                    val isToday = day.dateString == todayDateStr
                    val heightRatio = if (count > 0) (count.toFloat() / maxCompletions.toFloat()).coerceIn(0.15f, 1f) else 0.06f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 3.dp)
                    ) {
                        // Bar
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height((54 * heightRatio).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    when {
                                        count > 0 && isToday -> SkyBluePrimary
                                        count > 0 -> SkyBlueAccent
                                        isToday -> LightBluePillBg
                                        else -> Color(0xFFF1F5F9)
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Day number
                        Text(
                            text = "${day.dayNumber}",
                            color = if (isToday) SkyBluePrimary else TextDarkMuted,
                            fontSize = 9.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
