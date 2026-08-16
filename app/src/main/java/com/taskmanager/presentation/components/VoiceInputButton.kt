package com.taskmanager.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taskmanager.voice.VoiceInputController
import com.taskmanager.voice.VoiceInputState

/**
 * Mic button for voice Quick Add (issue 31). Emits the recognized text via
 * [onResult], ready to feed into the NLP parser (PR #5).
 */
@Composable
fun VoiceInputButton(
    onResult: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val controller = remember { VoiceInputController(context) }
    val state by controller.state.collectAsStateWithLifecycle()

    DisposableEffect(controller) {
        onDispose { controller.destroy() }
    }

    IconButton(onClick = {
        when (state) {
            VoiceInputState.Listening -> controller.stop()
            else -> {
                if (!controller.isAvailable) onError("Speech recognition not available")
                controller.startListening()
            }
        }
    }) {
        Icon(
            imageVector = if (state is VoiceInputState.Listening) Icons.Filled.Stop else Icons.Filled.Mic,
            contentDescription = "Voice input"
        )
    }

    when (val s = state) {
        is VoiceInputState.Result -> {
            if (s.text.isNotBlank()) onResult(s.text)
            controller.stop()
        }
        is VoiceInputState.Error -> onError(s.message)
        else -> {}
    }
}
