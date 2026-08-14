package com.taskmanager.domain.model

import java.time.LocalDate

data class HabitLog(
    val id: Long? = null,
    val habitId: Long,
    val date: LocalDate,
    val count: Int = 1,
    val completedAt: Long = System.currentTimeMillis()
)
