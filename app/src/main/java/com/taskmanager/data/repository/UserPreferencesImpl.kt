package com.taskmanager.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.taskmanager.domain.usecase.settings.UserPreferences
import javax.inject.Inject

class UserPreferencesImpl @Inject constructor(
    private val context: Context
) : UserPreferences {
    
    private val prefs: SharedPreferences =
        context.getSharedPreferences("taskmanager_prefs", Context.MODE_PRIVATE)

    override var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    override var hapticEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC_ENABLED, value).apply()

    override var baseCurrency: String
        get() = prefs.getString(KEY_BASE_CURRENCY, "RUB") ?: "RUB"
        set(value) = prefs.edit().putString(KEY_BASE_CURRENCY, value).apply()

    // ─── Pomodoro settings ───
    override var pomodoroWorkDuration: Int
        get() = prefs.getInt(KEY_POMO_WORK, 25)
        set(value) = prefs.edit().putInt(KEY_POMO_WORK, value.coerceIn(1, 120)).apply()

    override var pomodoroShortBreakDuration: Int
        get() = prefs.getInt(KEY_POMO_SHORT_BREAK, 5)
        set(value) = prefs.edit().putInt(KEY_POMO_SHORT_BREAK, value.coerceIn(1, 60)).apply()

    override var pomodoroLongBreakDuration: Int
        get() = prefs.getInt(KEY_POMO_LONG_BREAK, 15)
        set(value) = prefs.edit().putInt(KEY_POMO_LONG_BREAK, value.coerceIn(1, 60)).apply()

    override var pomodorosBeforeLongBreak: Int
        get() = prefs.getInt(KEY_POMO_BEFORE_LONG, 4)
        set(value) = prefs.edit().putInt(KEY_POMO_BEFORE_LONG, value.coerceIn(2, 10)).apply()

    override var pomodoroAutoStartBreaks: Boolean
        get() = prefs.getBoolean(KEY_POMO_AUTO_BREAKS, false)
        set(value) = prefs.edit().putBoolean(KEY_POMO_AUTO_BREAKS, value).apply()

    override var pomodoroAutoStartPomodoros: Boolean
        get() = prefs.getBoolean(KEY_POMO_AUTO_POMO, false)
        set(value) = prefs.edit().putBoolean(KEY_POMO_AUTO_POMO, value).apply()

    override var pomodoroSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_POMO_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_POMO_SOUND, value).apply()

    override var pomodoroVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_POMO_VIBRATION, true)
        set(value) = prefs.edit().putBoolean(KEY_POMO_VIBRATION, value).apply()

    override var pomodoroDailyGoal: Int
        get() = prefs.getInt(KEY_POMO_DAILY_GOAL, 8)
        set(value) = prefs.edit().putInt(KEY_POMO_DAILY_GOAL, value.coerceIn(1, 50)).apply()

    override val hasPin: Boolean
        get() = prefs.contains(KEY_PIN_HASH)

    override fun setPin(pin: String) {
        prefs.edit().putString(KEY_PIN_HASH, hashPin(pin)).apply()
    }

    override fun removePin() {
        prefs.edit().remove(KEY_PIN_HASH).apply()
    }

    override fun checkPin(pin: String): Boolean {
        val stored = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return hashPin(pin) == stored
    }

    private fun hashPin(pin: String): String {
        var hash = 0
        for (c in pin) {
            hash = hash * 31 + c.code
        }
        return hash.toString()
    }

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_HAPTIC_ENABLED = "haptic_enabled"
        private const val KEY_BASE_CURRENCY = "base_currency"
        // Pomodoro keys
        private const val KEY_POMO_WORK = "pomo_work_duration"
        private const val KEY_POMO_SHORT_BREAK = "pomo_short_break"
        private const val KEY_POMO_LONG_BREAK = "pomo_long_break"
        private const val KEY_POMO_BEFORE_LONG = "pomo_before_long_break"
        private const val KEY_POMO_AUTO_BREAKS = "pomo_auto_start_breaks"
        private const val KEY_POMO_AUTO_POMO = "pomo_auto_start_pomodoros"
        private const val KEY_POMO_SOUND = "pomo_sound_enabled"
        private const val KEY_POMO_VIBRATION = "pomo_vibration_enabled"
        private const val KEY_POMO_DAILY_GOAL = "pomo_daily_goal"
    }
}