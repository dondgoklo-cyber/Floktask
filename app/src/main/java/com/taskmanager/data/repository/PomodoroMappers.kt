package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.PomodoroSessionEntity
import com.taskmanager.domain.model.PomodoroSession
import com.taskmanager.domain.model.PomodoroType
import java.time.Instant

fun PomodoroSession.toEntity(): PomodoroSessionEntity = PomodoroSessionEntity(
    id = id ?: 0,
    taskId = taskId,
    startTime = startTime.toEpochMilli(),
    durationMinutes = durationMinutes,
    isCompleted = isCompleted,
    type = type.name
)

private fun String.toPomodoroType(): PomodoroType =
    runCatching { PomodoroType.valueOf(this) }.getOrDefault(PomodoroType.WORK)

fun PomodoroSessionEntity.toDomain(): PomodoroSession = PomodoroSession(
    id = id,
    taskId = taskId,
    startTime = Instant.ofEpochMilli(startTime),
    durationMinutes = durationMinutes,
    isCompleted = isCompleted,
    type = type.toPomodoroType()
)
