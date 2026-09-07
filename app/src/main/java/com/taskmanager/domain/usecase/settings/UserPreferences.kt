package com.taskmanager.domain.usecase.settings

import com.taskmanager.domain.logger.Logger
import javax.inject.Inject

/**
 * Interface for user preferences.
 * Part of Clean Architecture - Presentation layer depends only on abstractions, not concrete implementations.
 */
interface UserPreferences {
    /** User's display name */
    var userName: String
    
    /** Whether haptic feedback is enabled */
    var hapticEnabled: Boolean
    
    /** Base currency for finance operations (e.g., "RUB", "USD", "EUR") */
    var baseCurrency: String
    
    // ─── Pomodoro settings ───
    /** Work phase duration in minutes (default 25) */
    var pomodoroWorkDuration: Int
    /** Short break duration in minutes (default 5) */
    var pomodoroShortBreakDuration: Int
    /** Long break duration in minutes (default 15) */
    var pomodoroLongBreakDuration: Int
    /** Number of work sessions before a long break (default 4) */
    var pomodorosBeforeLongBreak: Int
    /** Auto-start break phases after work completes (default false) */
    var pomodoroAutoStartBreaks: Boolean
    /** Auto-start work phases after break completes (default false) */
    var pomodoroAutoStartPomodoros: Boolean
    /** Sound notification on phase completion (default true) */
    var pomodoroSoundEnabled: Boolean
    /** Vibration on phase completion (default true) */
    var pomodoroVibrationEnabled: Boolean
    /** Daily pomodoro goal (default 8) */
    var pomodoroDailyGoal: Int
    
    /** Whether a PIN has been set */
    val hasPin: Boolean
    
    fun setPin(pin: String)
    fun removePin()
    fun checkPin(pin: String): Boolean
}

class GetBaseCurrencyUseCase @Inject constructor(
    private val userPreferences: UserPreferences,
    private val logger: Logger
) {
    operator fun invoke(): String = userPreferences.baseCurrency
}