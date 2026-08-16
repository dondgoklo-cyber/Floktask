package com.taskmanager.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.taskmanager.security.UserPrefs
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Типы тактильного отклика для разных действий.
 */
enum class HapticType {
    /** Лёгкий отклик для обычного tap. */
    LIGHT,
    /** Отклик выбора (toggle, filter, tab). */
    SELECTION,
    /** Заметный отклик для завершения (task complete, habit done). */
    SUCCESS,
    /** Предупреждающий отклик для деструктивных действий. */
    WARNING
}

/**
 * Централизованный менеджер тактильного отклика.
 * Проверяет глобальную настройку [UserPrefs.hapticEnabled] перед выполнением.
 * Безопасен на устройствах без вибромотора.
 */
@Singleton
class HapticManager @Inject constructor(
    private val context: Context
) {
    private val prefs = UserPrefs(context)

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Выполняет тактильный отклик, если включён в настройках и устройство поддерживает.
     * Не блокирует UI — выполняется мгновенно.
     */
    fun perform(type: HapticType) {
        if (!prefs.hapticEnabled) return
        val v = vibrator ?: return
        if (!v.hasVibrator()) return

        val (amplitude, duration) = when (type) {
            HapticType.LIGHT -> 40 to 15L
            HapticType.SELECTION -> 60 to 20L
            HapticType.SUCCESS -> 100 to 30L
            HapticType.WARNING -> 120 to 40L
        }
        val safeAmplitude = amplitude.coerceIn(1, 255)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, safeAmplitude))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(duration)
        }
    }
}
