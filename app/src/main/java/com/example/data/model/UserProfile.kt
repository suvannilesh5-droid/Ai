package com.example.data.model

data class UserProfile(
    val email: String = "creator.autotube@gmail.com",
    val displayName: String = "Studio Creator",
    val avatarInitials: String = "SC",
    val isAuthenticated: Boolean = true,
    val authProvider: String = "Google OAuth 2.0",
    val channelName: String = "ToonMorph Studios 3D",
    val channelHandle: String = "@ToonMorph3D",
    val subscribersCount: String = "142.8K",
    val totalVideosUploaded: Int = 86,
    val morningShortsUploaded: Int = 43,
    val eveningLongsUploaded: Int = 43,
    val oauthScopesGranted: List<String> = listOf(
        "https://www.googleapis.com/auth/youtube.upload",
        "https://www.googleapis.com/auth/youtube.readonly",
        "https://www.googleapis.com/auth/userinfo.profile"
    )
)
