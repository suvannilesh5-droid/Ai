package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM video_projects ORDER BY createdAtEpoch DESC")
    fun getAllVideos(): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE id = :id")
    suspend fun getVideoById(id: Long): VideoProjectEntity?

    @Query("SELECT * FROM video_projects WHERE status = :status ORDER BY scheduledTimeEpoch ASC")
    fun getVideosByStatus(status: String): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE videoType = :videoType ORDER BY createdAtEpoch DESC LIMIT 10")
    fun getVideosByType(videoType: String): Flow<List<VideoProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoProjectEntity): Long

    @Update
    suspend fun updateVideo(video: VideoProjectEntity)

    @Query("DELETE FROM video_projects WHERE id = :id")
    suspend fun deleteVideoById(id: Long)

    @Query("SELECT COUNT(*) FROM video_projects")
    fun getTotalVideoCount(): Flow<Int>
}
