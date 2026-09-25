package com.example.data.model

data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String, // e.g. "Lofi Hip-Hop", "Synthwave", "Orchestral Toon", "Upbeat Bounce"
    val bpm: Int,
    val mood: String, // e.g. "Viral Trend / High Retention", "Playful & Whimsical", "Epic Adventure"
    val durationSeconds: Int,
    val isCopyrightFree: Boolean = true,
    val licenseBadge: String = "YouTube Safe / CC-BY-4.0",
    val viralTrendingRank: Int = 1, // 1 to 10 trending index on TikTok/Reels/Shorts
    val recommendedNiche: String = "All"
)
