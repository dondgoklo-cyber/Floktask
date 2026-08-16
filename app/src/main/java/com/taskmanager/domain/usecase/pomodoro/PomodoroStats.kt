package com.taskmanager.domain.usecase.pomodoro

data class PomodoroStats(
    val completedWorkSessions: Int = 0,
    val totalFocusMinutes: Int = 0,
    val completedToday: Int = 0
)
