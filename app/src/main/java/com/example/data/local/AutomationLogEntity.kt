package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automation_logs")
data class AutomationLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val stepIndex: Int, // 1 to 9 matching blueprint
    val stepName: String,
    val status: String, // "RUNNING", "SUCCESS", "WARNING", "FAILED"
    val videoType: String, // "SHORT", "LONG", or "SYSTEM"
    val message: String,
    val details: String? = null
)
