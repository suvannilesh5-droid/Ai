package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object Studio : Screen("studio", "AI Pipeline")
    object Schedule : Screen("schedule", "Scheduler")
    object Logs : Screen("logs", "Live Logs")
    object Channel : Screen("channel", "YouTube & OAuth")
    object VideoDetail : Screen("video_detail/{videoId}", "Video Details") {
        fun createRoute(videoId: Long) = "video_detail/$videoId"
    }
}
