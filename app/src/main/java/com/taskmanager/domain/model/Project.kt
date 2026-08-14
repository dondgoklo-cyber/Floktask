package com.taskmanager.domain.model

import java.time.Instant

data class Project(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val color: String? = null,
    val isArchived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
