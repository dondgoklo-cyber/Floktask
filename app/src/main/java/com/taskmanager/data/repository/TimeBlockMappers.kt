package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.TimeBlockEntity
import com.taskmanager.domain.model.TimeBlock
import java.time.Instant

fun TimeBlock.toEntity(): TimeBlockEntity = TimeBlockEntity(
    id = id ?: 0,
    title = title,
    taskId = taskId,
    projectId = projectId,
    startTime = startTime.toEpochMilli(),
    endTime = endTime.toEpochMilli(),
    color = color,
    isCompleted = isCompleted,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

fun TimeBlockEntity.toDomain(): TimeBlock = TimeBlock(
    id = id,
    title = title,
    taskId = taskId,
    projectId = projectId,
    startTime = Instant.ofEpochMilli(startTime),
    endTime = Instant.ofEpochMilli(endTime),
    color = color,
    isCompleted = isCompleted,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt)
)
