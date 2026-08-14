package com.taskmanager.domain.model

import java.time.Instant

data class Habit(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val color: String? = null,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    /** Дни недели для WEEKLY-частоты (1 = понедельник ... 7 = воскресенье). */
    val daysOfWeek: List<Int> = emptyList(),
    val targetCount: Int = 1,
    val reminderTime: Instant? = null,
    val isArchived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

enum class HabitFrequency {
    DAILY, WEEKLY, CUSTOM
}
