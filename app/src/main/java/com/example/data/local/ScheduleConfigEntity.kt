package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_config")
data class ScheduleConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val isAutomationActive: Boolean = true,
    val morningHour: Int = 8,
    val morningMinute: Int = 30,
    val morningNiche: String = "Viral 3D Cartoon Shorts",
    val morningAutoUpload: Boolean = true,
    val eveningHour: Int = 19,
    val eveningMinute: Int = 30,
    val eveningNiche: String = "Epic 3D Sci-Fi & Fable Long Form",
    val eveningAutoUpload: Boolean = true,
    val runInBackgroundWhenLocked: Boolean = true,
    val dynamicMusicSelection: Boolean = true,
    val deepMotionEnginePreset: String = "DeepMotion Animate 3D (Cartoon Rig)",
    val motionFps: Int = 30,
    val lastMorningRunTimestamp: Long = 0L,
    val lastEveningRunTimestamp: Long = 0L
)
