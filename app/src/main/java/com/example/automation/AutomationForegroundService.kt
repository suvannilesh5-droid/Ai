package com.example.automation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AutomationForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val CHANNEL_ID = "autotube_automation_channel"
        const val NOTIFICATION_ID = 2026
        const val ACTION_START_PIPELINE = "ACTION_START_PIPELINE"
        const val EXTRA_VIDEO_TYPE = "EXTRA_VIDEO_TYPE"

        fun startPipeline(context: Context, videoType: String) {
            try {
                val intent = Intent(context, AutomationForegroundService::class.java).apply {
                    action = ACTION_START_PIPELINE
                    putExtra(EXTRA_VIDEO_TYPE, videoType)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e("AutomationForegroundService", "Error starting foreground service: ${e.message}")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            createNotificationChannel()
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AutoTube::AutomationWakeLock").apply {
                setReferenceCounted(false)
            }
        } catch (e: Exception) {
            Log.e("AutomationForegroundService", "Error in onCreate", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val videoType = intent?.getStringExtra(EXTRA_VIDEO_TYPE) ?: "SHORT"
        try {
            val notification = buildNotification("AutoTube 3D Engine Active", "Preparing $videoType generation...")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Log.e("AutomationForegroundService", "startForeground failed", e)
        }

        try {
            wakeLock?.acquire(10 * 60 * 1000L) // 10 minutes maximum safe timeout
        } catch (e: Exception) {
            Log.e("AutomationForegroundService", "wakeLock acquire failed", e)
        }

        serviceScope.launch {
            try {
                val worker = PipelineWorker(applicationContext)
                worker.executePipeline(videoType) { step, stepName, desc ->
                    updateNotification("Step $step/9: $stepName", desc)
                }
                updateNotification("Pipeline Complete", "Video scheduled and rendered successfully!")
            } catch (e: Exception) {
                Log.e("AutomationForegroundService", "Error running worker", e)
            } finally {
                try {
                    if (wakeLock?.isHeld == true) {
                        wakeLock?.release()
                    }
                    stopForeground(STOP_FOREGROUND_DETACH)
                    stopSelf()
                } catch (e: Exception) {
                    Log.e("AutomationForegroundService", "Error stopping service", e)
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "AutoTube Automation Pipeline",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Runs background 3D cartoon video creation and upload scheduling"
                }
                val manager = getSystemService(NotificationManager::class.java)
                manager?.createNotificationChannel(channel)
            } catch (e: Exception) {
                Log.e("AutomationForegroundService", "Error creating notification channel", e)
            }
        }
    }

    private fun buildNotification(title: String, content: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.app_logo_icon)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        try {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(NOTIFICATION_ID, buildNotification(title, content))
        } catch (e: Exception) {
            Log.e("AutomationForegroundService", "Error updating notification", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            Log.e("AutomationForegroundService", "Error releasing wakeLock", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
