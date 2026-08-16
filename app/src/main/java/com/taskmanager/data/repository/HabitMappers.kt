package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.HabitEntity
import com.taskmanager.data.local.entity.HabitLogEntity
import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.model.HabitFrequency
import com.taskmanager.domain.model.HabitLog
import java.time.Instant
import java.time.LocalDate

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id ?: 0,
    name = name,
    description = description,
    icon = icon,
    color = color,
    frequency = frequency.name,
    daysOfWeek = daysOfWeek.joinToString(","),
    targetCount = targetCount,
    reminderTime = reminderTime?.toEpochMilli(),
    isArchived = isArchived,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

private fun String.toHabitFrequency(): HabitFrequency =
    runCatching { HabitFrequency.valueOf(this) }.getOrDefault(HabitFrequency.DAILY)

fun HabitEntity.toDomain(): Habit = Habit(
    id = id,
    name = name,
    description = description,
    icon = icon,
    color = color,
    frequency = frequency.toHabitFrequency(),
    daysOfWeek = daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() },
    targetCount = targetCount,
    reminderTime = reminderTime?.let { Instant.ofEpochMilli(it) },
    isArchived = isArchived,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt)
)

fun HabitLog.toEntity(): HabitLogEntity = HabitLogEntity(
    id = id ?: 0,
    habitId = habitId,
    date = date.toEpochDay(),
    count = count,
    completedAt = completedAt
)

fun HabitLogEntity.toDomain(): HabitLog = HabitLog(
    id = id,
    habitId = habitId,
    date = LocalDate.ofEpochDay(date),
    count = count,
    completedAt = completedAt
)
