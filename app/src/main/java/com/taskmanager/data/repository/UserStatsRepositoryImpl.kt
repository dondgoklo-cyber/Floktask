package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.UserStatsDao
import com.taskmanager.domain.model.Achievements
import com.taskmanager.domain.model.Achievement
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.UserStats
import com.taskmanager.domain.model.levelFromPoints
import com.taskmanager.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class UserStatsRepositoryImpl @Inject constructor(
    private val userStatsDao: UserStatsDao
) : UserStatsRepository {

    override fun observeStats(): Flow<UserStats?> =
        userStatsDao.observe().map { it?.toDomain() }

    override suspend fun getStats(): UserStats =
        userStatsDao.get()?.toDomain() ?: UserStats()

    override suspend fun saveStats(stats: UserStats) {
        userStatsDao.upsert(stats.toEntity())
    }

    override suspend fun unlockAchievement(achievement: Achievement): UserStats {
        val current = getStats()
        if (achievement.id in current.unlockedAchievementIds) return current
        val updated = current.copy(
            totalPoints = current.totalPoints + achievement.pointsReward,
            unlockedAchievementIds = current.unlockedAchievementIds + achievement.id,
            updatedAt = Instant.now()
        )
        return persist(updated)
    }

    override suspend fun addPoints(points: Long): UserStats {
        val current = getStats()
        return persist(current.copy(totalPoints = current.totalPoints + points))
    }

    override suspend fun recordTaskCompletion(priority: Priority): UserStats {
        val current = getStats()
        val basePoints = when (priority) {
            Priority.HIGH -> 15L
            Priority.MEDIUM -> 10L
            Priority.LOW -> 5L
            Priority.NONE -> 3L
        }
        val newCompleted = current.completedTasks + 1
        var updated = current.copy(
            totalPoints = current.totalPoints + basePoints,
            completedTasks = newCompleted,
            updatedAt = Instant.now()
        )

        if (newCompleted == 1) updated = unlockAchievementInternal(updated, Achievements.FIRST_TASK)
        if (newCompleted == 10) updated = unlockAchievementInternal(updated, Achievements.TEN_TASKS)
        if (newCompleted == 50) updated = unlockAchievementInternal(updated, Achievements.FIFTY_TASKS)
        if (priority == Priority.HIGH) updated = unlockAchievementInternal(updated, Achievements.HIGH_PRIORITY)

        return persist(updated)
    }

    private fun unlockAchievementInternal(stats: UserStats, achievement: Achievement): UserStats {
        if (achievement.id in stats.unlockedAchievementIds) return stats
        return stats.copy(
            totalPoints = stats.totalPoints + achievement.pointsReward,
            unlockedAchievementIds = stats.unlockedAchievementIds + achievement.id
        )
    }

    private suspend fun persist(stats: UserStats): UserStats {
        val withLevel = stats.copy(level = levelFromPoints(stats.totalPoints))
        userStatsDao.upsert(withLevel.toEntity())
        return withLevel
    }
}
