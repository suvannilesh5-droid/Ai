package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.AutomationLogEntity
import com.example.data.local.ScheduleConfigEntity
import com.example.data.local.VideoProjectEntity
import kotlinx.coroutines.flow.Flow

class VideoRepository(
    private val database: AppDatabase
) {
    val allVideos: Flow<List<VideoProjectEntity>> = database.videoDao().getAllVideos()
    val scheduleConfigFlow: Flow<ScheduleConfigEntity?> = database.scheduleDao().getScheduleConfigFlow()
    val allLogsFlow: Flow<List<AutomationLogEntity>> = database.logDao().getAllLogs()
    val totalVideosCountFlow: Flow<Int> = database.videoDao().getTotalVideoCount()

    suspend fun getVideoById(id: Long): VideoProjectEntity? {
        return database.videoDao().getVideoById(id)
    }

    suspend fun saveVideoProject(video: VideoProjectEntity): Long {
        return database.videoDao().insertVideo(video)
    }

    suspend fun updateVideoProject(video: VideoProjectEntity) {
        database.videoDao().updateVideo(video)
    }

    suspend fun deleteVideoProject(id: Long) {
        database.videoDao().deleteVideoById(id)
    }

    suspend fun getScheduleConfig(): ScheduleConfigEntity {
        return database.scheduleDao().getScheduleConfig() ?: ScheduleConfigEntity()
    }

    suspend fun updateScheduleConfig(config: ScheduleConfigEntity) {
        database.scheduleDao().insertOrUpdateConfig(config)
    }

    suspend fun logStep(
        stepIndex: Int,
        stepName: String,
        status: String,
        videoType: String,
        message: String,
        details: String? = null
    ) {
        database.logDao().insertLog(
            AutomationLogEntity(
                stepIndex = stepIndex,
                stepName = stepName,
                status = status,
                videoType = videoType,
                message = message,
                details = details
            )
        )
    }

    suspend fun clearLogs() {
        database.logDao().clearAllLogs()
    }
}
