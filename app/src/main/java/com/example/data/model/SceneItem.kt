package com.example.data.model

data class SceneItem(
    val sceneNumber: Int,
    val title: String,
    val prompt: String,
    val voiceover: String,
    val durationSeconds: Int,
    val motionStyle: String, // e.g. "Dynamic Run & Jump", "Expressive Talking", "Cinematic Pan"
    val cameraAngle: String, // e.g. "Low Angle Close-up", "Wide Establishing Shot", "Over-the-shoulder"
    val lightingMood: String, // e.g. "Neon Golden Hour", "Moody Studio Rim Light"
    val characterAction: String,
    val visualAssetRes: String? = null
)
