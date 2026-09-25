package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AccountProfileScreen
import com.example.ui.screens.CreatePipelineScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LogHistoryScreen
import com.example.ui.screens.SchedulerScreen
import com.example.ui.screens.VideoDetailScreen
import com.example.ui.theme.AutoTubeTheme
import com.example.ui.viewmodel.AutoTubeViewModel

enum class MainTab(val title: String) {
    DASHBOARD("Dashboard"),
    STUDIO("AI Studio"),
    SCHEDULE("Scheduler"),
    LOGS("Logs"),
    ACCOUNT("Account")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoTubeTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(viewModel: AutoTubeViewModel = viewModel()) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(MainTab.DASHBOARD) }
    var selectedVideoId by remember { mutableStateOf<Long?>(null) }

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* Handled */ }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val pipelineState by viewModel.pipelineState.collectAsStateWithLifecycle()
    val scheduleConfig by viewModel.scheduleConfig.collectAsStateWithLifecycle()
    val videoProjects by viewModel.videoProjects.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val isVoicePlaying by viewModel.isVoicePlaying.collectAsStateWithLifecycle()

    val selectedVideo = videoProjects.find { it.id == selectedVideoId }

    if (selectedVideo != null) {
        VideoDetailScreen(
            video = selectedVideo,
            isVoicePlaying = isVoicePlaying,
            onBack = { selectedVideoId = null },
            onDelete = {
                viewModel.deleteVideo(selectedVideo.id)
                selectedVideoId = null
            },
            onPlayVoiceover = { viewModel.playVoiceover(it) },
            onStopVoiceover = { viewModel.stopVoiceover() }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == MainTab.DASHBOARD,
                        onClick = { currentTab = MainTab.DASHBOARD },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        modifier = Modifier.testTag("nav_item_dashboard")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.STUDIO,
                        onClick = { currentTab = MainTab.STUDIO },
                        icon = { Icon(Icons.Default.Animation, contentDescription = "Studio") },
                        label = { Text("Studio") },
                        modifier = Modifier.testTag("nav_item_studio")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.SCHEDULE,
                        onClick = { currentTab = MainTab.SCHEDULE },
                        icon = { Icon(Icons.Default.Schedule, contentDescription = "Schedule") },
                        label = { Text("Timers") },
                        modifier = Modifier.testTag("nav_item_schedule")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.LOGS,
                        onClick = { currentTab = MainTab.LOGS },
                        icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Logs") },
                        label = { Text("Logs") },
                        modifier = Modifier.testTag("nav_item_logs")
                    )
                    NavigationBarItem(
                        selected = currentTab == MainTab.ACCOUNT,
                        onClick = { currentTab = MainTab.ACCOUNT },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Account") },
                        label = { Text("OAuth") },
                        modifier = Modifier.testTag("nav_item_account")
                    )
                }
            }
        ) { innerPadding ->
            val contentModifier = Modifier.padding(innerPadding)

            when (currentTab) {
                MainTab.DASHBOARD -> DashboardScreen(
                    userProfile = userProfile,
                    scheduleConfig = scheduleConfig,
                    pipelineState = pipelineState,
                    videoProjects = videoProjects,
                    onTriggerPipeline = { type ->
                        viewModel.triggerPipeline(type)
                        currentTab = MainTab.STUDIO
                    },
                    onNavigateToStudio = { currentTab = MainTab.STUDIO },
                    onNavigateToSchedule = { currentTab = MainTab.SCHEDULE },
                    onSelectVideo = { id -> selectedVideoId = id },
                    modifier = contentModifier
                )
                MainTab.STUDIO -> CreatePipelineScreen(
                    pipelineState = pipelineState,
                    userProfile = userProfile,
                    isVoicePlaying = isVoicePlaying,
                    onTriggerPipeline = { type -> viewModel.triggerPipeline(type) },
                    onPlayVoiceover = { viewModel.playVoiceover(it) },
                    onStopVoiceover = { viewModel.stopVoiceover() },
                    modifier = contentModifier
                )
                MainTab.SCHEDULE -> SchedulerScreen(
                    scheduleConfig = scheduleConfig,
                    onSaveSchedule = { isAuto, mH, mM, mN, eH, eM, eN, dynMusic, rig, bgRun ->
                        viewModel.updateScheduleConfig(isAuto, mH, mM, mN, eH, eM, eN, dynMusic, rig, bgRun)
                    },
                    modifier = contentModifier
                )
                MainTab.LOGS -> LogHistoryScreen(
                    logs = logs,
                    onClearLogs = { viewModel.clearLogs() },
                    modifier = contentModifier
                )
                MainTab.ACCOUNT -> AccountProfileScreen(
                    userProfile = userProfile,
                    onLogin = { email, name -> viewModel.loginWithGoogle(email, name) },
                    onLogout = { viewModel.logout() },
                    modifier = contentModifier
                )
            }
        }
    }
}
