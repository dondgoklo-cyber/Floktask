package com.taskmanager.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Outcome of a voice recognition session (issue 31: voice input for Quick Add).
 */
sealed class VoiceInputState {
    data object Idle : VoiceInputState()
    data object Listening : VoiceInputState()
    data class Result(val text: String) : VoiceInputState()
    data class Error(val message: String) : VoiceInputState()
}

/**
 * Wraps Android [SpeechRecognizer] into a [StateFlow] for Compose consumption.
 * Must be created with an Activity/ApplicationContext (SpeechRecognizer availability).
 */
class VoiceInputController(private val context: Context) {

    private val _state = MutableStateFlow<VoiceInputState>(VoiceInputState.Idle)
    val state: StateFlow<VoiceInputState> = _state.asStateFlow()

    private var recognizer: SpeechRecognizer? = null

    val isAvailable: Boolean get() = SpeechRecognizer.isRecognitionAvailable(context)

    fun startListening() {
        if (!isAvailable) {
            _state.value = VoiceInputState.Error("Speech recognition not available")
            return
        }
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {
                    _state.value = VoiceInputState.Error(errorDescription(error))
                }

                override fun onResults(results: Bundle?) {
                    val matches = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        .orEmpty()
                    _state.value = VoiceInputState.Result(matches.firstOrNull().orEmpty())
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
        _state.value = VoiceInputState.Listening
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your task")
        }
        recognizer?.startListening(intent)
    }

    fun stop() {
        recognizer?.stopListening()
        _state.value = VoiceInputState.Idle
    }

    fun destroy() {
        recognizer?.destroy()
        recognizer = null
        _state.value = VoiceInputState.Idle
    }

    private fun errorDescription(code: Int): String = when (code) {
        SpeechRecognizer.ERROR_NO_MATCH -> "No speech heard"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timed out"
        SpeechRecognizer.ERROR_AUDIO -> "Audio error"
        SpeechRecognizer.ERROR_CLIENT -> "Client error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Record audio permission required"
        else -> "Recognition error ($code)"
    }
}
