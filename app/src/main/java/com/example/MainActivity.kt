package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.HabitViewModel
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.MatrixScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.HabitBloomTheme
import com.example.ui.theme.LightBluePillBg
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfacePureWhite
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.WhiteBackground
import com.example.util.NotificationHelper

enum class ScreenTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    TODAY("Today", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, "nav_tab_today"),
    MATRIX("Matrix", Icons.Filled.GridOn, Icons.Outlined.GridOn, "nav_tab_matrix"),
    AI_COACH("AI Coach", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_tab_ai_coach"),
    REMINDERS("Reminders", Icons.Filled.NotificationsActive, Icons.Outlined.NotificationsActive, "nav_tab_reminders")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.initChannel(this)

        setContent {
            HabitBloomTheme {
                HabitsBookApp()
            }
        }
    }
}

@Composable
fun HabitsBookApp(viewModel: HabitViewModel = viewModel()) {
    var currentTab by remember { mutableStateOf(ScreenTab.TODAY) }

    Scaffold(
        containerColor = WhiteBackground,
        bottomBar = {
            NavigationBar(
                containerColor = SurfacePureWhite,
                tonalElevation = 2.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .background(SurfacePureWhite)
                    .border(1.dp, SurfaceBorderLight)
                    .testTag("bottom_nav_bar")
            ) {
                ScreenTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                tint = if (isSelected) SkyBluePrimary else TextDarkMuted
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                color = if (isSelected) SkyBluePrimary else TextDarkMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = LightBluePillBg,
                            selectedIconColor = SkyBluePrimary,
                            unselectedIconColor = TextDarkMuted
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.TODAY -> TodayScreen(viewModel = viewModel)
                ScreenTab.MATRIX -> MatrixScreen(viewModel = viewModel)
                ScreenTab.AI_COACH -> AiCoachScreen(viewModel = viewModel)
                ScreenTab.REMINDERS -> RemindersScreen(viewModel = viewModel)
            }
        }
    }
}
