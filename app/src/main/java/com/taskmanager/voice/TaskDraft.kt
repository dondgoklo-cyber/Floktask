package com.taskmanager.voice

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.RecurrenceRule
import java.time.LocalDate
import java.time.LocalTime

/**
 * Промежуточная модель после парсинга голосового ввода.
 * Показывается пользователю для подтверждения перед созданием задачи.
 */
data class TaskDraft(
    val title: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val projectName: String? = null,
    val tags: List<String> = emptyList(),
    val priority: Priority = Priority.NONE,
    val durationMinutes: Long? = null,
    val recurrenceRule: RecurrenceRule? = null,
    val rawText: String = "",
    val isAmbiguousTime: Boolean = false
)
