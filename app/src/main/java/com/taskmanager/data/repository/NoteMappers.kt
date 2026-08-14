package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.NoteEntity
import com.taskmanager.domain.model.Note
import org.json.JSONArray
import java.time.Instant

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id ?: 0,
    title = title,
    contentMarkdown = contentMarkdown,
    folderId = folderId,
    tags = if (tags.isNotEmpty()) JSONArray(tags).toString() else null,
    pinned = pinned,
    archived = archived,
    projectId = projectId,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

fun NoteEntity.toDomain(): Note = Note(
    id = id,
    title = title,
    contentMarkdown = contentMarkdown,
    folderId = folderId,
    tags = parseNoteTags(tags),
    pinned = pinned,
    archived = archived,
    projectId = projectId,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt)
)

private fun parseNoteTags(json: String?): List<String> {
    if (json.isNullOrBlank()) return emptyList()
    return runCatching {
        val arr = JSONArray(json)
        (0 until arr.length()).map { arr.getString(it) }
    }.getOrDefault(emptyList())
}
