package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.ui.components.getHabitIcon
import com.example.ui.theme.LightBluePillBg
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfaceOffWhite
import com.example.ui.theme.SurfacePureWhite
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, timeOfDay: String, iconName: String, targetGoal: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("daily") }
    var selectedTimeOfDay by remember { mutableStateOf("morning") }
    var selectedIcon by remember { mutableStateOf("water") }
    var targetGoalText by remember { mutableStateOf("30") }

    val categories = listOf("daily" to "Daily", "weekly" to "Weekly", "monthly" to "Monthly")
    val timesOfDay = listOf("morning" to "Morning", "afternoon" to "Afternoon", "evening" to "Evening", "night" to "Night")
    val icons = listOf("water", "bed", "sun", "book", "run", "meditate", "spa", "phone", "clean", "coffee", "restaurant", "family")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfacePureWhite,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderLight, RoundedCornerShape(16.dp))
                .testTag("add_habit_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "New Routine Habit",
                    style = MaterialTheme.typography.titleLarge,
                    color = SkyBluePrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Add to your Habits Book planner",
                    color = TextDarkMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title") },
                    placeholder = { Text("e.g. Read 20 pages, Hydrate 2L") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyBluePrimary,
                        unfocusedBorderColor = SurfaceBorderLight,
                        focusedTextColor = TextDarkPrimary,
                        unfocusedTextColor = TextDarkPrimary,
                        focusedContainerColor = SurfacePureWhite,
                        unfocusedContainerColor = SurfacePureWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_habit_title")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Frequency / Category
                Text(text = "Cadence", color = TextDarkSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { (catId, label) ->
                        val isSelected = selectedCategory == catId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SkyBluePrimary else SurfaceOffWhite)
                                .border(1.dp, if (isSelected) SkyBluePrimary else SurfaceBorderLight, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedCategory = catId
                                    if (catId == "weekly") targetGoalText = "4"
                                    if (catId == "monthly") targetGoalText = "1"
                                    if (catId == "daily") targetGoalText = "30"
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextDarkPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Time of day
                Text(text = "Routine Block", color = TextDarkSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    timesOfDay.forEach { (timeId, label) ->
                        val isSelected = selectedTimeOfDay == timeId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) LightBluePillBg else SurfaceOffWhite)
                                .border(1.dp, if (isSelected) SkyBluePrimary else SurfaceBorderLight, RoundedCornerShape(8.dp))
                                .clickable { selectedTimeOfDay = timeId }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) SkyBluePrimary else TextDarkSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Icon selection
                Text(text = "Icon Symbol", color = TextDarkSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icons.forEach { iconKey ->
                        val isSelected = selectedIcon == iconKey
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SkyBluePrimary else SurfaceOffWhite)
                                .border(1.dp, if (isSelected) SkyBluePrimary else SurfaceBorderLight, RoundedCornerShape(8.dp))
                                .clickable { selectedIcon = iconKey },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getHabitIcon(iconKey),
                                contentDescription = iconKey,
                                tint = if (isSelected) Color.White else SkyBlueAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDarkSecondary)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val goal = targetGoalText.toIntOrNull() ?: 30
                                onSave(title, selectedCategory, selectedTimeOfDay, selectedIcon, goal)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkyBluePrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("button_save_habit")
                    ) {
                        Text("Save Habit", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
