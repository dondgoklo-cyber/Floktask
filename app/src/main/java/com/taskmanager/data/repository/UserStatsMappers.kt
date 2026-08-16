package com.taskmanager.data.repository

import com.taskmanager.data.local.entity.UserStatsEntity
import com.taskmanager.domain.model.UserStats
import java.time.Instant

fun UserStatsEntity.toDomain(): UserStats = UserStats(
    id = id,
    totalPoints = totalPoints,
    level = level,
    completedTasks = completedTasks,
    streak = streak,
    unlockedAchievementIds = unlockedAchievements
        .split(",")
        .filter { it.isNotBlank() },
    updatedAt = Instant.ofEpochMilli(updatedAt)
)

fun UserStats.toEntity(): UserStatsEntity = UserStatsEntity(
    id = id,
    totalPoints = totalPoints,
    level = level,
    completedTasks = completedTasks,
    streak = streak,
    unlockedAchievements = unlockedAchievementIds.joinToString(","),
    updatedAt = updatedAt.toEpochMilli()
)
