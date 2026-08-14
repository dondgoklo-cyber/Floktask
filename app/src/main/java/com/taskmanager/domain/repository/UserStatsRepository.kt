package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Achievement
import com.taskmanager.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserStatsRepository {
    fun observeStats(): Flow<UserStats?>
    suspend fun getStats(): UserStats?
    suspend fun saveStats(stats: UserStats)
    suspend fun unlockAchievement(achievement: Achievement): UserStats
    suspend fun addPoints(points: Long): UserStats
    suspend fun recordTaskCompletion(priority: com.taskmanager.domain.model.Priority): UserStats
}
