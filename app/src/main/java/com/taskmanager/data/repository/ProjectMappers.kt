package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.ProjectEntity
import com.taskmanager.domain.model.Project
import java.time.Instant

fun Project.toEntity(): ProjectEntity = ProjectEntity(
    id = id ?: 0,
    title = title,
    description = description,
    color = color,
    isArchived = isArchived,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

fun ProjectEntity.toDomain(): Project = Project(
    id = id,
    title = title,
    description = description,
    color = color,
    isArchived = isArchived,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt)
)
