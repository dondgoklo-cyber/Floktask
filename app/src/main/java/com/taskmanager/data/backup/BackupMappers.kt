package com.taskmanager.data.backup

import com.taskmanager.data.backup.dto.BackupDataDto
import com.taskmanager.data.backup.dto.BackupFileDto
import com.taskmanager.data.backup.dto.BackupMetadataDto
import com.taskmanager.data.backup.dto.ProjectDto
import com.taskmanager.data.backup.dto.TagDto
import com.taskmanager.data.backup.dto.TaskDto
import com.taskmanager.domain.model.BackupData
import com.taskmanager.domain.model.BackupFile
import com.taskmanager.domain.model.BackupMetadata
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.RecurrenceRule
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.model.Task
import java.time.Instant

fun Task.toDto(): TaskDto = TaskDto(
    id = id,
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

fun Project.toDto(): ProjectDto = ProjectDto(
    id = id,
    title = title,
    description = description,
    color = color,
    isArchived = isArchived,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

fun Tag.toDto(): TagDto = TagDto(id, name, color)

fun BackupData.toDto(): BackupDataDto = BackupDataDto(
    tasks = tasks.map { it.toDto() },
    projects = projects.map { it.toDto() },
    tags = tags.map { it.toDto() }
)

fun TaskDto.toDomain(): Task {
    val priority = Priority.entries.firstOrNull { it.value == priority } ?: Priority.NONE
    val recurrence = recurrenceRule?.let { runCatching { RecurrenceRule.valueOf(it) }.getOrNull() }
    return Task(
        id = id,
        title = title,
        description = description,
        projectId = projectId,
        priority = priority,
        deadline = deadline?.let { Instant.ofEpochMilli(it) },
        isCompleted = isCompleted,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt),
        color = color,
        reminderDate = reminderDate?.let { Instant.ofEpochMilli(it) },
        recurrenceRule = recurrence
    )
}

fun ProjectDto.toDomain(): Project = Project(
    id = id,
    title = title,
    description = description,
    color = color,
    isArchived = isArchived,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt)
)

fun TagDto.toDomain(): Tag = Tag(id = id, name = name, color = color)

fun BackupDataDto.toDomain(): BackupData = BackupData(
    tasks = tasks.map { it.toDomain() },
    projects = projects.map { it.toDomain() },
    tags = tags.map { it.toDomain() }
)

fun BackupFileDto.toDomain(): BackupFile = BackupFile(
    metadata = metadata.toDomain(),
    data = data.toDomain()
)

fun BackupMetadata.toDto(): BackupMetadataDto = BackupMetadataDto(
    schemaVersion = schemaVersion,
    appVersion = appVersion,
    createdAt = createdAt.toEpochMilli(),
    deviceName = deviceName
)

fun BackupMetadataDto.toDomain(): BackupMetadata = BackupMetadata(
    schemaVersion = schemaVersion,
    appVersion = appVersion,
    createdAt = Instant.ofEpochMilli(createdAt),
    deviceName = deviceName
)

fun BackupFile.toDto(): BackupFileDto = BackupFileDto(
    metadata = metadata.toDto(),
    data = data.toDto()
)
