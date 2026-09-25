package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsPlayer(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentUtterance = MutableStateFlow("")
    val currentUtterance: StateFlow<String> = _currentUtterance.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlaying.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isPlaying.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isPlaying.value = false
                }
            })
        } catch (e: Exception) {
            Log.e("TtsPlayer", "TextToSpeech initialization error (safe fallback active)", e)
            tts = null
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("TtsPlayer", "Language not supported or missing data")
                } else {
                    isInitialized = true
                    tts?.setPitch(1.05f)
                    tts?.setSpeechRate(1.02f)
                }
            } catch (e: Exception) {
                Log.e("TtsPlayer", "Error configuring TTS language", e)
            }
        }
    }

    fun speak(text: String, utteranceId: String = "scene_vo") {
        if (text.isBlank()) return
        _currentUtterance.value = text
        if (isInitialized && tts != null) {
            try {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            } catch (e: Exception) {
                Log.e("TtsPlayer", "Error speaking text", e)
            }
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TtsPlayer", "Error stopping TTS", e)
        }
        _isPlaying.value = false
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("TtsPlayer", "Error shutting down TTS", e)
        }
        tts = null
    }
}
