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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.HabitBloomGreen
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
fun DailyCheckInDialog(
    initialMood: String = "Happy",
    initialSleep: Float = 7.5f,
    initialWeather: String = "Sunny",
    initialReflection: String = "",
    onDismiss: () -> Unit,
    onSave: (mood: String, sleep: Float, weather: String, note: String) -> Unit
) {
    var selectedMood by remember { mutableStateOf(initialMood) }
    var sleepHours by remember { mutableFloatStateOf(initialSleep) }
    var selectedWeather by remember { mutableStateOf(initialWeather) }
    var reflectionNote by remember { mutableStateOf(initialReflection) }

    val moods = listOf(
        "Happy" to "😊",
        "Loved" to "🥰",
        "Calm" to "😌",
        "Okay" to "😐",
        "Tired" to "🥱",
        "Sad" to "😢",
        "Stressed" to "😫"
    )

    val weathers = listOf(
        "Sunny" to "☀️",
        "Partly cloudy" to "⛅",
        "Cloudy" to "☁️",
        "Rainy" to "🌧️",
        "Stormy" to "⛈️",
        "Windy" to "💨"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfacePureWhite,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SurfaceBorderLight, RoundedCornerShape(16.dp))
                .testTag("daily_check_in_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Daily Reflection & Lifestyle",
                    style = MaterialTheme.typography.titleLarge,
                    color = SkyBluePrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Track how habits influence your mood & energy",
                    color = TextDarkMuted,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mood selector
                Text(text = "Mood of Today", color = TextDarkSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moods.forEach { (moodName, emoji) ->
                        val isSelected = selectedMood.equals(moodName, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SkyBluePrimary else SurfaceOffWhite)
                                .border(1.dp, if (isSelected) SkyBluePrimary else SurfaceBorderLight, RoundedCornerShape(8.dp))
                                .clickable { selectedMood = moodName }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$emoji $moodName",
                                color = if (isSelected) Color.White else TextDarkPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sleep hours with sleep guide
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Sleep: ", color = TextDarkSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${String.format("%.1f", sleepHours)} hours",
                        color = SkyBluePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    val guideText = when {
                        sleepHours >= 7.0f -> "Great for focus & mood ✨"
                        sleepHours >= 6.0f -> "A little short"
                        else -> "Too little, rest up!"
                    }
                    Text(text = guideText, color = HabitBloomGreen, fontSize = 11.sp)
                }

                Slider(
                    value = sleepHours,
                    onValueChange = { sleepHours = it },
                    valueRange = 4.0f..11.0f,
                    steps = 13,
                    colors = SliderDefaults.colors(
                        thumbColor = SkyBluePrimary,
                        activeTrackColor = SkyBlueAccent,
                        inactiveTrackColor = Color(0xFFE2E8F0)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Weather selector
                Text(text = "Weather", color = TextDarkSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    weathers.forEach { (weatherName, emoji) ->
                        val isSelected = selectedWeather.equals(weatherName, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) LightBluePillBg else SurfaceOffWhite)
                                .border(1.dp, if (isSelected) SkyBluePrimary else SurfaceBorderLight, RoundedCornerShape(8.dp))
                                .clickable { selectedWeather = weatherName }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$emoji $weatherName",
                                color = if (isSelected) SkyBluePrimary else TextDarkSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reflection note
                OutlinedTextField(
                    value = reflectionNote,
                    onValueChange = { reflectionNote = it },
                    label = { Text("Daily Reflection / Learning") },
                    placeholder = { Text("e.g. Small habits stack up faster than I think!") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkyBluePrimary,
                        unfocusedBorderColor = SurfaceBorderLight,
                        focusedTextColor = TextDarkPrimary,
                        unfocusedTextColor = TextDarkPrimary,
                        focusedContainerColor = SurfacePureWhite,
                        unfocusedContainerColor = SurfacePureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Buttons
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
                            onSave(selectedMood, sleepHours, selectedWeather, reflectionNote)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkyBluePrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("button_save_checkin")
                    ) {
                        Text("Save Check-in", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
