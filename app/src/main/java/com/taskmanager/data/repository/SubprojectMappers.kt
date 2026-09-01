package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.SubprojectEntity
import com.taskmanager.domain.model.Subproject
import java.time.Instant

fun Subproject.toEntity(): SubprojectEntity = SubprojectEntity(
    id = id ?: 0,
    title = title,
    description = description,
    parentProjectId = parentProjectId,
    parentSubprojectId = parentSubprojectId,
    color = color,
    icon = icon,
    deadline = deadline?.toEpochMilli(),
    isArchived = isArchived,
    orderIndex = orderIndex,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

fun SubprojectEntity.toDomain(): Subproject = Subproject(
    id = id,
    title = title,
    description = description,
    parentProjectId = parentProjectId,
    parentSubprojectId = parentSubprojectId,
    color = color,
    icon = icon,
    deadline = deadline?.let { Instant.ofEpochMilli(it) },
    isArchived = isArchived,
    orderIndex = orderIndex,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt)
)
