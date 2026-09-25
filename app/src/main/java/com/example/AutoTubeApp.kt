package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.example.automation.AlarmScheduler
import com.example.automation.AutomationForegroundService
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutoTubeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            createNotificationChannels()
        } catch (e: Exception) {
            Log.e("AutoTubeApp", "Error initializing notification channels", e)
        }

        // Initialize database and schedule alarms safely in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(this@AutoTubeApp)
                val config = db.scheduleDao().getScheduleConfig()
                if (config != null && config.isAutomationActive) {
                    AlarmScheduler.scheduleTimers(this@AutoTubeApp, config)
                }
            } catch (e: Exception) {
                Log.e("AutoTubeApp", "Error during background app init", e)
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AutomationForegroundService.CHANNEL_ID,
                "AutoTube Automation Pipeline",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Runs background 3D cartoon video creation and upload scheduling"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
