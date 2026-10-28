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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.ui.components.MatrixGridView
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
fun MatrixScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.habits.collectAsState()
    val monthLogs by viewModel.monthLogs.collectAsState()
    val monthCheckIns by viewModel.monthCheckIns.collectAsState()

    var selectedFilter by remember { mutableStateOf("all") }
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showClearConfirmationDialog by remember { mutableStateOf(false) }

    val filteredHabits = remember(habits, selectedFilter) {
        when (selectedFilter) {
            "daily" -> habits.filter { it.category == "daily" }
            "weekly" -> habits.filter { it.category == "weekly" }
            "monthly" -> habits.filter { it.category == "monthly" }
            else -> habits
        }
    }

    val filterOptions = listOf(
        "all" to "All (${habits.size})",
        "daily" to "Daily",
        "weekly" to "Weekly",
        "monthly" to "Monthly"
    )

    Scaffold(
        containerColor = WhiteBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddHabitDialog = true },
                containerColor = SkyBluePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_habit_matrix")
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
            // Header with Maker Branding
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "HABITS PLANNER MATRIX",
                                style = MaterialTheme.typography.titleMedium,
                                color = SkyBluePrimary,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(LightBluePillBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Made by Riddham",
                                    color = SkyBluePrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = DateUtils.getMonthYearDisplay(),
                            color = TextDarkSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Reset / Clear Ticks button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceOffWhite)
                            .border(1.dp, SurfaceBorderLight, RoundedCornerShape(8.dp))
                            .clickable { showClearConfirmationDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("button_matrix_clear_ticks")
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
                                text = "Clear Ticks",
                                color = SkyBluePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filterOptions.forEach { (key, label) ->
                        val isSelected = selectedFilter == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SkyBluePrimary else SurfacePureWhite)
                                .border(1.dp, if (isSelected) SkyBluePrimary else SurfaceBorderLight, RoundedCornerShape(10.dp))
                                .clickable { selectedFilter = key }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextDarkSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Matrix Grid Table (with frozen/pinned left column for habit names!)
            item {
                MatrixGridView(
                    habits = filteredHabits,
                    monthLogs = monthLogs,
                    monthCheckIns = monthCheckIns,
                    todayDateStr = viewModel.todayDateString,
                    onToggleCell = { habitId, dateStr ->
                        viewModel.toggleHabit(habitId, dateStr)
                    }
                )
            }

            // Matrix Legend & Tips
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfacePureWhite)
                        .border(1.dp, SurfaceBorderLight, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "FROZEN COLUMN MATRIX",
                            color = SkyBluePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Habit names remain pinned on the left as you scroll days 1–31 to the right.\n" +
                                   "• Tap any checkbox cell to mark or unmark your habit completion.\n" +
                                   "• Reset ticks anytime using the 'Clear Ticks' button above.",
                            color = TextDarkMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showClearConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmationDialog = false },
            title = { Text("Clear All Ticks?", color = TextDarkPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will clear all checkmarks so you can log everything yourself from day one.", color = TextDarkSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllTicks()
                        showClearConfirmationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                ) {
                    Text("Clear All Ticks")
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
}
