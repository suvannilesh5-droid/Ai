package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.TtsPlayer
import com.example.automation.AlarmScheduler
import com.example.automation.AutomationForegroundService
import com.example.automation.PipelineWorker
import com.example.data.local.AppDatabase
import com.example.data.local.AutomationLogEntity
import com.example.data.local.ScheduleConfigEntity
import com.example.data.local.VideoProjectEntity
import com.example.data.model.MusicTrack
import com.example.data.model.SceneItem
import com.example.data.model.UserProfile
import com.example.data.repository.MusicRepository
import com.example.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PipelineState(
    val isRunning: Boolean = false,
    val currentStepIndex: Int = 0, // 0 means idle, 1..9
    val currentStepName: String = "",
    val currentStepDescription: String = "",
    val currentVideoType: String = "SHORT",
    val activeScenes: List<SceneItem> = emptyList(),
    val activeMusicTrack: MusicTrack? = null,
    val generatedVideo: VideoProjectEntity? = null,
    val error: String? = null
)

class AutoTubeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val videoRepository = VideoRepository(database)
    private val musicRepository = MusicRepository()
    private val ttsPlayer = TtsPlayer(application)
    private val pipelineWorker = PipelineWorker(application)

    // User Profile & OAuth
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Automation & Pipeline State
    private val _pipelineState = MutableStateFlow(PipelineState())
    val pipelineState: StateFlow<PipelineState> = _pipelineState.asStateFlow()

    // Database Flows
    val videoProjects: StateFlow<List<VideoProjectEntity>> = videoRepository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scheduleConfig: StateFlow<ScheduleConfigEntity> = videoRepository.scheduleConfigFlow
        .map { it ?: ScheduleConfigEntity() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ScheduleConfigEntity()
        )

    val logs: StateFlow<List<AutomationLogEntity>> = videoRepository.allLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isVoicePlaying: StateFlow<Boolean> = ttsPlayer.isPlaying

    init {
        // Prepare initial sample project if empty
        viewModelScope.launch {
            val existing = videoRepository.getScheduleConfig()
            AlarmScheduler.scheduleTimers(getApplication(), existing)
        }
    }

    fun triggerPipeline(videoType: String) {
        if (_pipelineState.value.isRunning) return

        _pipelineState.value = PipelineState(
            isRunning = true,
            currentStepIndex = 1,
            currentStepName = "User Authentication",
            currentStepDescription = "Verifying Google OAuth session...",
            currentVideoType = videoType
        )

        // Also trigger foreground service to ensure background persistence even if app is minimized
        AutomationForegroundService.startPipeline(getApplication(), videoType)

        viewModelScope.launch {
            val result = pipelineWorker.executePipeline(videoType) { step, stepName, desc ->
                _pipelineState.value = _pipelineState.value.copy(
                    currentStepIndex = step,
                    currentStepName = stepName,
                    currentStepDescription = desc
                )
            }

            result.fold(
                onSuccess = { video ->
                    val scenes = PipelineWorker.deserializeScenes(video.scenesJson)
                    _pipelineState.value = _pipelineState.value.copy(
                        isRunning = false,
                        currentStepIndex = 9,
                        currentStepName = "Pipeline Completed",
                        currentStepDescription = "Rendered and scheduled successfully!",
                        activeScenes = scenes,
                        activeMusicTrack = musicRepository.getAllTracks().find { it.title == video.bgmTitle } ?: musicRepository.selectTrendingViralTrack(),
                        generatedVideo = video
                    )
                },
                onFailure = { err ->
                    _pipelineState.value = _pipelineState.value.copy(
                        isRunning = false,
                        error = err.localizedMessage
                    )
                }
            )
        }
    }

    fun updateScheduleConfig(
        isAutomationActive: Boolean,
        morningHour: Int,
        morningMinute: Int,
        morningNiche: String,
        eveningHour: Int,
        eveningMinute: Int,
        eveningNiche: String,
        dynamicMusic: Boolean,
        deepMotionRig: String,
        runInBackground: Boolean
    ) {
        viewModelScope.launch {
            val updated = scheduleConfig.value.copy(
                isAutomationActive = isAutomationActive,
                morningHour = morningHour,
                morningMinute = morningMinute,
                morningNiche = morningNiche,
                eveningHour = eveningHour,
                eveningMinute = eveningMinute,
                eveningNiche = eveningNiche,
                dynamicMusicSelection = dynamicMusic,
                deepMotionEnginePreset = deepMotionRig,
                runInBackgroundWhenLocked = runInBackground
            )
            videoRepository.updateScheduleConfig(updated)
            AlarmScheduler.scheduleTimers(getApplication(), updated)
        }
    }

    fun loginWithGoogle(email: String, name: String) {
        _userProfile.value = _userProfile.value.copy(
            email = email,
            displayName = name,
            isAuthenticated = true
        )
    }

    fun logout() {
        _userProfile.value = _userProfile.value.copy(
            isAuthenticated = false
        )
    }

    fun playVoiceover(text: String) {
        ttsPlayer.speak(text)
    }

    fun stopVoiceover() {
        ttsPlayer.stop()
    }

    fun clearLogs() {
        viewModelScope.launch {
            videoRepository.clearLogs()
        }
    }

    fun deleteVideo(id: Long) {
        viewModelScope.launch {
            videoRepository.deleteVideoProject(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsPlayer.release()
    }
}
