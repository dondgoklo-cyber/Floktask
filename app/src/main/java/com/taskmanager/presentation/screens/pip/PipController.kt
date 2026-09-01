package com.taskmanager.presentation.screens.pip

import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Enters Picture-in-Picture mode for the Pomodoro timer (issue 35: timer
 * disappeared when minimizing). Call from an Activity while it's active.
 *
 * Returns true if PiP was entered (or already in PiP), false if unsupported
 * on this API level.
 */
class PipController {

    fun enterPip(activity: Activity, aspectRatio: Int = 3, aspectRatioDenom: Int = 4): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(aspectRatio, aspectRatioDenom))
            .build()
        return runCatching {
            activity.enterPictureInPictureMode(params)
            true
        }.getOrDefault(false)
    }

    fun isInPip(activity: Activity): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) activity.isInPictureInPictureMode else false
}

/**
 * Enters PiP when [enabled] turns true and the activity goes to background.
 * Use in a Compose hierarchy that lives in the Activity hosting the timer.
 */
@Composable
fun RememberEnterPipOnBackground(enabled: Boolean) {
    val context = LocalContext.current
    DisposableEffect(enabled, context) {
        if (!enabled) {
            onDispose { }
        } else {
            onDispose {
                val activity = ContextCompat.getActivity(context) ?: return@onDispose
                PipController().enterPip(activity)
            }
        }
    }
}
