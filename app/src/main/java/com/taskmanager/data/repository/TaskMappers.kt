package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.TaskEntity
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.model.TaskStatus
import java.time.Instant

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id ?: 0,
    title = title,
    description = description,
    projectId = projectId,
    priority = priority.value,
    status = status.name,
    deadline = deadline?.toEpochMilli(),
    startTime = startTime?.toEpochMilli(),
    durationMinutes = durationMinutes,
    pomodoroEstimate = pomodoroEstimate,
    timeEstimateMinutes = timeEstimateMinutes,
    eisenhowerQuadrant = eisenhowerQuadrant?.name,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
    color = color,
    reminderDate = reminderDate?.toEpochMilli(),
    recurrenceRule = recurrenceRule?.name
)

private fun Int.toPriority(): Priority =
    Priority.entries.firstOrNull { it.value == this } ?: Priority.NONE

private fun String.toRecurrenceRule(): RecurrenceRule? =
    runCatching { RecurrenceRule.valueOf(this) }.getOrNull()

private fun String.toTaskStatus(): TaskStatus =
    runCatching { TaskStatus.valueOf(this) }.getOrDefault(TaskStatus.TODO)

private fun String.toEisenhowerQuadrant(): EisenhowerQuadrant? =
    runCatching { EisenhowerQuadrant.valueOf(this) }.getOrNull()

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    projectId = projectId,
    priority = priority.toPriority(),
    status = status.toTaskStatus(),
    deadline = deadline?.let { Instant.ofEpochMilli(it) },
    startTime = startTime?.let { Instant.ofEpochMilli(it) },
    durationMinutes = durationMinutes,
    pomodoroEstimate = pomodoroEstimate,
    timeEstimateMinutes = timeEstimateMinutes,
    eisenhowerQuadrant = eisenhowerQuadrant?.toEisenhowerQuadrant(),
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    color = color,
    reminderDate = reminderDate?.let { Instant.ofEpochMilli(it) },
    recurrenceRule = recurrenceRule?.toRecurrenceRule(),
    tags = emptyList()
)
