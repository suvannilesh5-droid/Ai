package com.example.data.repository

import com.example.data.model.MusicTrack
import java.util.concurrent.atomic.AtomicInteger

class MusicRepository {

    // Curated catalog of 100% royalty-free, non-copyright, YouTube/Meta safe tracks
    private val tracks = listOf(
        MusicTrack(
            id = "bgm_viral_01",
            title = "Galactic Bounce (Viral Trend)",
            artist = "NeonBeat Studio",
            genre = "Synthwave / Lo-Fi Bounce",
            bpm = 128,
            mood = "High Energy Viral Hook",
            durationSeconds = 60,
            isCopyrightFree = true,
            licenseBadge = "CC0 / 100% Monetizable",
            viralTrendingRank = 1,
            recommendedNiche = "Viral 3D Cartoon Shorts"
        ),
        MusicTrack(
            id = "bgm_viral_02",
            title = "Whimsical Mischief 3D",
            artist = "ToonGroove Collective",
            genre = "Orchestral Cartoon Jazz",
            bpm = 118,
            mood = "Funny & Quirky Animated",
            durationSeconds = 60,
            isCopyrightFree = true,
            licenseBadge = "YouTube Safe / Royalty Free",
            viralTrendingRank = 2,
            recommendedNiche = "Comedy Cartoon"
        ),
        MusicTrack(
            id = "bgm_viral_03",
            title = "Cyber Odyssey Chronicles",
            artist = "Aether Wave",
            genre = "Cinematic Ambient Sci-Fi",
            bpm = 95,
            mood = "Epic Narrative & Suspense",
            durationSeconds = 240,
            isCopyrightFree = true,
            licenseBadge = "CC-BY Safe (Auto Attributed)",
            viralTrendingRank = 3,
            recommendedNiche = "Sci-Fi & Animated Fable Mysteries"
        ),
        MusicTrack(
            id = "bgm_viral_04",
            title = "Sunset Coffee Chills",
            artist = "Tokyo Lo-Fi Lab",
            genre = "Lo-Fi Beats",
            bpm = 85,
            mood = "Chill & Relaxed Storytelling",
            durationSeconds = 180,
            isCopyrightFree = true,
            licenseBadge = "YouTube Audio Library Clean",
            viralTrendingRank = 4,
            recommendedNiche = "Daily Life Satire"
        ),
        MusicTrack(
            id = "bgm_viral_05",
            title = "Hyperdrive Heroics",
            artist = "Arcade Pulse",
            genre = "Electro Funk / 8-Bit Chiptune",
            bpm = 135,
            mood = "Action-Packed Climax",
            durationSeconds = 90,
            isCopyrightFree = true,
            licenseBadge = "CC0 Safe / YouTube Monetized",
            viralTrendingRank = 5,
            recommendedNiche = "Action & Adventure"
        ),
        MusicTrack(
            id = "bgm_viral_06",
            title = "Magic Forest Fable",
            artist = "FairyTale Strings",
            genre = "Acoustic Folk Toon",
            bpm = 100,
            mood = "Emotional & Wholesome",
            durationSeconds = 210,
            isCopyrightFree = true,
            licenseBadge = "CC0 No Copyright",
            viralTrendingRank = 6,
            recommendedNiche = "Moral Fables"
        )
    )

    private val rotationCounter = AtomicInteger(0)

    fun getAllTracks(): List<MusicTrack> = tracks

    /**
     * 7.2 Dynamic Music Selection: Selects a different track for every new video generated
     */
    fun selectNextDynamicTrack(videoType: String): MusicTrack {
        val filtered = if (videoType == "SHORT") {
            tracks.filter { it.durationSeconds <= 90 }
        } else {
            tracks.filter { it.durationSeconds >= 120 }
        }
        val pool = if (filtered.isNotEmpty()) filtered else tracks
        val index = (rotationCounter.getAndIncrement()) % pool.size
        return pool[index]
    }

    /**
     * 7.3 Trending/Viral Music Selection: Prioritizes current #1 or top viral trending audio
     */
    fun selectTrendingViralTrack(): MusicTrack {
        return tracks.minByOrNull { it.viralTrendingRank } ?: tracks.first()
    }

    fun getTrackById(id: String): MusicTrack? {
        return tracks.find { it.id == id }
    }
}
