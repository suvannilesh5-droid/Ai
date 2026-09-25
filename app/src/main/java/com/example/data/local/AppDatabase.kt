package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        VideoProjectEntity::class,
        ScheduleConfigEntity::class,
        AutomationLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun videoDao(): VideoDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun logDao(): LogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "autotube_studio_database.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Initialize default schedule and sample system log directly via SQL to prevent any deadlock
                            try {
                                db.execSQL("""
                                    INSERT OR IGNORE INTO schedule_config (
                                        id, isAutomationActive, morningHour, morningMinute, morningNiche, morningAutoUpload,
                                        eveningHour, eveningMinute, eveningNiche, eveningAutoUpload, runInBackgroundWhenLocked,
                                        dynamicMusicSelection, deepMotionEnginePreset, motionFps, lastMorningRunTimestamp, lastEveningRunTimestamp
                                    ) VALUES (
                                        1, 1, 8, 30, 'Viral 3D Cartoon Shorts', 1,
                                        19, 30, 'Sci-Fi & Animated Fable Mysteries', 1, 1,
                                        1, 'DeepMotion Animate 3D (Cartoon Rig)', 30, 0, 0
                                    )
                                """.trimIndent())

                                db.execSQL("""
                                    INSERT INTO automation_logs (
                                        timestamp, stepIndex, stepName, status, videoType, message, details
                                    ) VALUES (
                                        ${System.currentTimeMillis()}, 1, 'System Init', 'SUCCESS', 'SYSTEM',
                                        'AutoTube Engine v3.8 initialized with Google OAuth & DeepMotion 3D pipelines.', NULL
                                    )
                                """.trimIndent())
                            } catch (e: Exception) {
                                android.util.Log.e("AppDatabase", "Error executing initial DB seed", e)
                            }
                        }
                    })
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
