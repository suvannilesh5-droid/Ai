package com.example.automation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.local.ScheduleConfigEntity
import java.util.Calendar

object AlarmScheduler {

    const val ACTION_TRIGGER_PIPELINE = "com.aistudio.autotube.ACTION_TRIGGER_PIPELINE"
    const val EXTRA_VIDEO_TYPE = "EXTRA_VIDEO_TYPE" // "SHORT" or "LONG"
    private const val REQUEST_CODE_MORNING = 1001
    private const val REQUEST_CODE_EVENING = 1002

    fun scheduleTimers(context: Context, config: ScheduleConfigEntity) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

            if (!config.isAutomationActive) {
                cancelAlarms(context)
                return
            }

            // Schedule Morning Short
            val morningTime = getNextTriggerMillis(config.morningHour, config.morningMinute)
            val morningIntent = Intent(context, AutomationAlarmReceiver::class.java).apply {
                action = ACTION_TRIGGER_PIPELINE
                putExtra(EXTRA_VIDEO_TYPE, "SHORT")
            }
            val morningPendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_MORNING,
                morningIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            scheduleAlarmSafely(alarmManager, morningTime, morningPendingIntent)
            Log.d("AlarmScheduler", "Scheduled Morning Short for: ${Calendar.getInstance().apply { timeInMillis = morningTime }.time}")

            // Schedule Evening Long
            val eveningTime = getNextTriggerMillis(config.eveningHour, config.eveningMinute)
            val eveningIntent = Intent(context, AutomationAlarmReceiver::class.java).apply {
                action = ACTION_TRIGGER_PIPELINE
                putExtra(EXTRA_VIDEO_TYPE, "LONG")
            }
            val eveningPendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_EVENING,
                eveningIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            scheduleAlarmSafely(alarmManager, eveningTime, eveningPendingIntent)
            Log.d("AlarmScheduler", "Scheduled Evening Long for: ${Calendar.getInstance().apply { timeInMillis = eveningTime }.time}")
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Failed to schedule timers", e)
        }
    }

    private fun scheduleAlarmSafely(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        try {
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            if (canScheduleExact) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else {
                // Inexact alarm safe fallback when SCHEDULE_EXACT_ALARM permission is not granted
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.w("AlarmScheduler", "Exact alarm not permitted by system, falling back to inexact alarm", e)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } catch (fallbackError: Exception) {
                Log.e("AlarmScheduler", "Fallback alarm also failed", fallbackError)
            }
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error in scheduleAlarmSafely", e)
        }
    }

    private fun getNextTriggerMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // If time already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    fun cancelAlarms(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val morningIntent = Intent(context, AutomationAlarmReceiver::class.java).apply { action = ACTION_TRIGGER_PIPELINE }
            val eveningIntent = Intent(context, AutomationAlarmReceiver::class.java).apply { action = ACTION_TRIGGER_PIPELINE }

            val p1 = PendingIntent.getBroadcast(context, REQUEST_CODE_MORNING, morningIntent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            p1?.let { alarmManager.cancel(it) }

            val p2 = PendingIntent.getBroadcast(context, REQUEST_CODE_EVENING, eveningIntent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            p2?.let { alarmManager.cancel(it) }
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error in cancelAlarms", e)
        }
    }
}
