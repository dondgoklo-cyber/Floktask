package com.taskmanager.domain.model

import java.time.Instant

data class Subtask(
    val id: Long? = null,
    val taskId: Long,
    val parentSubtaskId: Long? = null,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0,
    val createdAt: Instant = Instant.now(),
    val children: List<Subtask> = emptyList()
)
