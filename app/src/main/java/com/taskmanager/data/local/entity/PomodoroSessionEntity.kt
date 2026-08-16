package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pomodoro_sessions",
    indices = [
        Index("taskId"),
        Index("type"),
        Index("startTime"),
        Index("isCompleted")
    ]
)
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long?,
    val startTime: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val type: String = "WORK"
)
