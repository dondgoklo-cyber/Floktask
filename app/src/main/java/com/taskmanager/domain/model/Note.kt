package com.taskmanager.domain.model

import java.time.Instant

data class Note(
    val id: Long? = null,
    val title: String,
    val contentMarkdown: String = "",
    val folderId: Long? = null,
    val tags: List<String> = emptyList(),
    val pinned: Boolean = false,
    val archived: Boolean = false,
    val projectId: Long? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
