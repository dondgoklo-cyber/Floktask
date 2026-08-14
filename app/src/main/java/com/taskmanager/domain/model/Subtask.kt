package com.taskmanager.domain.model

import java.time.Instant

data class Subtask(
    val id: Long? = null,
    val taskId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0,
    val createdAt: Instant = Instant.now()
)
