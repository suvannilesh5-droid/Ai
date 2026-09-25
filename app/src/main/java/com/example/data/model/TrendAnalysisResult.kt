package com.example.data.model

data class TrendAnalysisResult(
    val platformFocus: String, // "YouTube, Instagram, Facebook Multi-Platform"
    val trendingTopic: String,
    val audienceDemandScore: Int, // 1 to 100
    val targetEmotion: String, // "Wonder & Curiosity", "Laughter & Relief", "Suspense"
    val viralHook: String,
    val coreStoryline: String,
    val suggestedNiche: String,
    val competitorKeywords: List<String>
)
