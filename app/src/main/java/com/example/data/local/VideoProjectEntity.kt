package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val tags: String, // Comma separated tags for YouTube SEO
    val videoType: String, // "SHORT" (Morning) or "LONG" (Evening)
    val aspectRatio: String, // "9:16" or "16:9"
    val durationSeconds: Int,
    val marketNiche: String,
    val trendSource: String, // "YouTube Shorts + IG Reels + FB Viral"
    val viralHook: String,
    val storyScript: String,
    val scenesJson: String, // Serialized scenes
    val motionEngine: String = "DeepMotion 3D Cartoon v4.2",
    val motionRig: String = "Stylized Cartoon Biped",
    val bgmTitle: String,
    val bgmArtist: String,
    val bgmMood: String,
    val bgmCopyrightFree: Boolean = true,
    val youtubeVisibility: String = "PUBLIC", // "PUBLIC", "UNLISTED", "PRIVATE"
    val status: String, // "DRAFT", "PROCESSED", "SCHEDULED", "UPLOADED"
    val scheduledTimeEpoch: Long,
    val uploadedTimeEpoch: Long? = null,
    val youtubeVideoId: String? = null,
    val viewCountEstimate: String = "0",
    val createdAtEpoch: Long = System.currentTimeMillis()
)
