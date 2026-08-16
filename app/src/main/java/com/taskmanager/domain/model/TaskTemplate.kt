package com.taskmanager.domain.model

/**
 * A reusable task template with pre-filled fields (issue 29: users create
 * the same tasks repeatedly — "Weekly review", "Morning routine").
 */
data class TaskTemplate(
    val id: Long? = null,
    val name: String,
    val title: String,
    val description: String? = null,
    val priority: Priority = Priority.NONE,
    val defaultDurationMinutes: Int? = null,
    val defaultTags: List<String> = emptyList(),
    val defaultProjectName: String? = null
)
