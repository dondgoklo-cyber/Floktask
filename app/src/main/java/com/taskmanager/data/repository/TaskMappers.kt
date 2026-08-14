package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.TaskEntity
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Task
import java.time.Instant

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id ?: 0,
    title = title,
    description = description,
    projectId = projectId,
    priority = priority.value,
    deadline = deadline?.toEpochMilli(),
    isCompleted = isCompleted,
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

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    projectId = projectId,
    priority = priority.toPriority(),
    deadline = deadline?.let { Instant.ofEpochMilli(it) },
    isCompleted = isCompleted,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    color = color,
    reminderDate = reminderDate?.let { Instant.ofEpochMilli(it) },
    recurrenceRule = recurrenceRule?.toRecurrenceRule()
)
