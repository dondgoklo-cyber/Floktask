package com.taskmanager.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for haptic feedback (vibration) across the app.
 * Provides different vibration patterns for different user actions.
 */
@Singleton
class HapticManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private val hasVibrator: Boolean by lazy {
        vibrator.hasVibrator()
    }

    /**
     * Light vibration for small interactions (button press, toggle)
     */
    fun lightVibrate() {
        if (!hasVibrator) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    20,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(20)
        }
    }

    /**
     * Medium vibration for important actions (task creation, drag & drop complete)
     */
    fun mediumVibrate() {
        if (!hasVibrator) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    40,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(40)
        }
    }

    /**
     * Heavy vibration for critical actions (task deletion, error states)
     */
    fun heavyVibrate() {
        if (!hasVibrator) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    60,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(60)
        }
    }

    /**
     * Success feedback - short double tap
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun successVibrate() {
        if (!hasVibrator) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 20, 10, 20),
                    intArrayOf(0, 255, 0, 255),
                    -1
                )
            )
        } else {
            lightVibrate()
        }
    }

    /**
     * Error feedback - long pulse
     */
    fun errorVibrate() {
        if (!hasVibrator) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    80,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(80)
        }
    }

    /**
     * Check if haptic feedback is available
     */
    fun isAvailable(): Boolean = hasVibrator

    /**
     * Vibrate based on action type
     */
    fun vibrate(action: HapticAction) {
        when (action) {
            HapticAction.LIGHT -> lightVibrate()
            HapticAction.MEDIUM -> mediumVibrate()
            HapticAction.HEAVY -> heavyVibrate()
            HapticAction.SUCCESS -> successVibrate()
            HapticAction.ERROR -> errorVibrate()
        }
    }

    /**
     * Perform haptic feedback - alias for vibrate() to match old API
     */
    fun perform(type: HapticAction) = vibrate(type)
}

/**
 * Backward compatibility type alias
 */
typealias HapticType = HapticAction

/**
 * Types of haptic feedback actions
 */
enum class HapticAction {
    LIGHT,      // Button press, checkbox toggle
    MEDIUM,     // Drag & drop complete, task creation
    HEAVY,      // Task deletion, important actions
    SUCCESS,    // Successful operation
    ERROR,      // Error state, failed operation
    SELECTION   // Item selection, navigation
}
