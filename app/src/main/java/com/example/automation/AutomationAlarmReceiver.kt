package com.example.automation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AutomationAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val videoType = intent.getStringExtra(AlarmScheduler.EXTRA_VIDEO_TYPE) ?: "SHORT"
        Log.d("AutomationAlarmReceiver", "Alarm triggered for video type: $videoType")
        AutomationForegroundService.startPipeline(context, videoType)
    }
}
