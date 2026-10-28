package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfacePureWhite
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkSecondary

@Composable
fun HabitBloomPlantView(
    bloomProgress: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = bloomProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "bloomAnimation"
    )

    Box(
        modifier = modifier
            .testTag("habit_bloom_container")
            .clip(RoundedCornerShape(14.dp))
            .background(SurfacePureWhite)
            .border(1.dp, SurfaceBorderLight, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "TODAY'S BLOOM",
                    style = MaterialTheme.typography.labelLarge,
                    color = SkyBluePrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightBluePillBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}% Bloomed",
                        color = SkyBluePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Canvas drawing the plant growing & blooming
            Box(
                modifier = Modifier
                    .size(width = 160.dp, height = 105.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val groundY = h - 14.dp.toPx()
                    val centerX = w / 2

                    // 1. Pot base / ground
                    drawRoundRect(
                        color = Color(0xFFE2E8F0),
                        topLeft = Offset(centerX - 40.dp.toPx(), groundY),
                        size = Size(80.dp.toPx(), 7.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                    drawRoundRect(
                        color = SkyBlueAccent.copy(alpha = 0.35f),
                        topLeft = Offset(centerX - 34.dp.toPx(), groundY - 2.dp.toPx()),
                        size = Size(68.dp.toPx(), 3.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )

                    // 2. Stem height based on progress
                    val stemTopY = groundY - (animatedProgress * 62.dp.toPx()).coerceAtLeast(8.dp.toPx())
                    val stemPath = Path().apply {
                        moveTo(centerX, groundY)
                        cubicTo(
                            centerX - 3.dp.toPx(), groundY - 18.dp.toPx(),
                            centerX + 3.dp.toPx(), stemTopY + 18.dp.toPx(),
                            centerX, stemTopY
                        )
                    }
                    drawPath(
                        path = stemPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(SkyBlueAccent, HabitBloomGreen)
                        ),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 3. Leaves appear as progress increases
                    if (animatedProgress > 0.15f) {
                        val leafProgress = ((animatedProgress - 0.15f) / 0.85f).coerceIn(0f, 1f)
                        val leafSize = 15.dp.toPx() * leafProgress

                        // Left leaf
                        val leftLeaf = Path().apply {
                            val ly = groundY - 22.dp.toPx()
                            moveTo(centerX, ly)
                            quadraticTo(centerX - leafSize * 1.3f, ly - leafSize * 0.8f, centerX - leafSize * 1.5f, ly)
                            quadraticTo(centerX - leafSize * 0.8f, ly + leafSize * 0.4f, centerX, ly)
                        }
                        drawPath(leftLeaf, color = HabitBloomGreen)

                        // Right leaf
                        if (animatedProgress > 0.35f) {
                            val rightLeaf = Path().apply {
                                val ry = groundY - 35.dp.toPx()
                                moveTo(centerX, ry)
                                quadraticTo(centerX + leafSize * 1.3f, ry - leafSize * 0.8f, centerX + leafSize * 1.5f, ry)
                                quadraticTo(centerX + leafSize * 0.8f, ry + leafSize * 0.4f, centerX, ry)
                            }
                            drawPath(rightLeaf, color = SkyBlueAccent)
                        }
                    }

                    // 4. Blooming flower bud / petals at top
                    if (animatedProgress > 0.45f) {
                        val flowerProgress = ((animatedProgress - 0.45f) / 0.55f).coerceIn(0f, 1f)
                        val flowerRadius = 13.dp.toPx() * flowerProgress

                        // Soft light blue radial aura
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(LightBlueSoft.copy(alpha = 0.35f * flowerProgress), Color.Transparent),
                                center = Offset(centerX, stemTopY),
                                radius = flowerRadius * 2.2f
                            ),
                            center = Offset(centerX, stemTopY),
                            radius = flowerRadius * 2.2f
                        )

                        // Center bud
                        drawCircle(
                            color = SkyBlueAccent,
                            center = Offset(centerX, stemTopY),
                            radius = flowerRadius * 0.6f
                        )

                        // Top & side petals
                        drawCircle(
                            color = SkyBluePrimary.copy(alpha = 0.85f),
                            center = Offset(centerX, stemTopY - flowerRadius * 0.8f),
                            radius = flowerRadius * 0.5f
                        )
                        drawCircle(
                            color = LightBlueSoft.copy(alpha = 0.85f),
                            center = Offset(centerX - flowerRadius * 0.7f, stemTopY - flowerRadius * 0.3f),
                            radius = flowerRadius * 0.45f
                        )
                        drawCircle(
                            color = LightBlueSoft.copy(alpha = 0.85f),
                            center = Offset(centerX + flowerRadius * 0.7f, stemTopY - flowerRadius * 0.3f),
                            radius = flowerRadius * 0.45f
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Every habit you complete helps it grow.",
                color = TextDarkSecondary,
                fontSize = 12.sp
            )
        }
    }
}
