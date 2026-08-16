package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.SubtaskEntity
import com.taskmanager.domain.model.Subtask
import java.time.Instant

fun Subtask.toEntity(): SubtaskEntity = SubtaskEntity(
    id = id ?: 0,
    taskId = taskId,
    title = title,
    isCompleted = isCompleted,
    orderIndex = orderIndex,
    createdAt = createdAt.toEpochMilli()
)

fun SubtaskEntity.toDomain(): Subtask = Subtask(
    id = id,
    taskId = taskId,
    title = title,
    isCompleted = isCompleted,
    orderIndex = orderIndex,
    createdAt = Instant.ofEpochMilli(createdAt)
)
