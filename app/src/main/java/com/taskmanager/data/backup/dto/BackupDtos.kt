package com.taskmanager.data.backup.dto

import com.google.gson.annotations.SerializedName

data class TaskDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("projectId") val projectId: Long?,
    @SerializedName("priority") val priority: Int,
    @SerializedName("deadline") val deadline: Long?,
    @SerializedName("isCompleted") val isCompleted: Boolean,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("updatedAt") val updatedAt: Long,
    @SerializedName("color") val color: String?,
    @SerializedName("reminderDate") val reminderDate: Long?,
    @SerializedName("recurrenceRule") val recurrenceRule: String?
)

data class ProjectDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("color") val color: String?,
    @SerializedName("isArchived") val isArchived: Boolean,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("updatedAt") val updatedAt: Long
)

data class TagDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String?
)

data class BackupDataDto(
    @SerializedName("tasks") val tasks: List<TaskDto>,
    @SerializedName("projects") val projects: List<ProjectDto>,
    @SerializedName("tags") val tags: List<TagDto>
)

data class BackupMetadataDto(
    @SerializedName("schemaVersion") val schemaVersion: Int,
    @SerializedName("appVersion") val appVersion: String,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("deviceName") val deviceName: String?
)

data class BackupFileDto(
    @SerializedName("metadata") val metadata: BackupMetadataDto,
    @SerializedName("data") val data: BackupDataDto
)
