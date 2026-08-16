package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pomodoro_sessions",
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = ["id"],
        childColumns = ["taskId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("taskId"), Index("createdAt")]
)
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long? = null,
    val startTime: Long,
    val durationMinutes: Int,
    val isCompleted: Boolean = false,
    val type: String = "WORK",
    val createdAt: Long = System.currentTimeMillis()
)
