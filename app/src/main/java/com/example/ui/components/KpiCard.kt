package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HabitBloomGreen
import com.example.ui.theme.LightBluePillBg
import com.example.ui.theme.LightBlueSoft
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.StreakFlame
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfacePureWhite
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary

@Composable
fun ProgressRing(
    percentage: Float, // 0..1
    color: Color,
    sizeDp: Int = 62,
    strokeWidthDp: Int = 7
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "ringAnimation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(sizeDp.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(sizeDp.dp)) {
            val stroke = Stroke(width = strokeWidthDp.dp.toPx(), cap = StrokeCap.Round)
            // Background track
            drawArc(
                color = Color(0xFFE2E8F0),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )
            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * animatedPercentage,
                useCenter = false,
                style = stroke
            )
        }
        Text(
            text = "${(animatedPercentage * 100).toInt()}%",
            color = TextDarkPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TopKpiRow(
    dailyProgress: Float,
    dailyCompletedText: String,
    weeklyProgress: Float,
    weeklyCompletedText: String,
    monthlyProgress: Float,
    monthlyCompletedText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_kpi_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KpiCardItem(
            title = "DAILY HABITS",
            progress = dailyProgress,
            subtitle = dailyCompletedText,
            color = SkyBlueAccent,
            modifier = Modifier.weight(1f)
        )
        KpiCardItem(
            title = "WEEKLY HABITS",
            progress = weeklyProgress,
            subtitle = weeklyCompletedText,
            color = SkyBluePrimary,
            modifier = Modifier.weight(1f)
        )
        KpiCardItem(
            title = "MONTHLY GOALS",
            progress = monthlyProgress,
            subtitle = monthlyCompletedText,
            color = LightBlueSoft,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun KpiCardItem(
    title: String,
    progress: Float,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfacePureWhite)
            .border(1.dp, SurfaceBorderLight, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                color = TextDarkSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProgressRing(percentage = progress, color = color)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = TextDarkMuted,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightBluePillBg)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "On track 🚀",
                    color = SkyBluePrimary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MonthlyHighlightsCard(
    bestStreakDays: Int,
    perfectDaysCount: Int,
    avgSleepHours: Float,
    dominantMood: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_highlights_card")
            .clip(RoundedCornerShape(14.dp))
            .background(SurfacePureWhite)
            .border(1.dp, SurfaceBorderLight, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = "MONTH AT A GLANCE",
                style = MaterialTheme.typography.labelSmall,
                color = SkyBluePrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HighlightStat(
                    icon = { Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streak", tint = StreakFlame, modifier = Modifier.size(18.dp)) },
                    value = "$bestStreakDays days",
                    label = "Best Streak"
                )
                HighlightStat(
                    icon = { Icon(Icons.Default.Star, contentDescription = "100% days", tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp)) },
                    value = "$perfectDaysCount days",
                    label = "100% Days"
                )
                HighlightStat(
                    icon = { Icon(Icons.Default.Bedtime, contentDescription = "Avg sleep", tint = SkyBluePrimary, modifier = Modifier.size(18.dp)) },
                    value = "${String.format("%.1f", avgSleepHours)}h",
                    label = "Avg Sleep"
                )
                HighlightStat(
                    icon = { Icon(Icons.Default.SentimentSatisfiedAlt, contentDescription = "Mood", tint = HabitBloomGreen, modifier = Modifier.size(18.dp)) },
                    value = dominantMood,
                    label = "Top Mood"
                )
            }
        }
    }
}

@Composable
private fun HighlightStat(
    icon: @Composable () -> Unit,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = value, color = TextDarkPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = TextDarkMuted, fontSize = 10.sp)
    }
}
