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
import android.util.Log

class UserStatsRepositoryImpl @Inject constructor(
    private val userStatsDao: UserStatsDao
) : UserStatsRepository {

    override fun observeStats(): Flow<UserStats?> =
        userStatsDao.observe().map { it?.toDomain() }

    override suspend fun getStats(): UserStats = try {
        userStatsDao.get()?.toDomain() ?: UserStats()
    } catch (e: Exception) {
        Log.e("UserStatsRepositoryImpl", "Error in UserStats", e)
        throw e
    }

    override suspend fun saveStats(stats: UserStats) {
        try {
            userStatsDao.upsert(stats.toEntity())
        } catch (e: Exception) {
            Log.e("UserStatsRepositoryImpl", "Error in UserStats", e)
            throw e
        }
    }

    override suspend fun unlockAchievement(achievement: Achievement): UserStats {
        val current = try {
            getStats()
        } catch (e: Exception) {
            Log.e("UserStatsRepositoryImpl", "Error in current", e)
            throw e
        }
        if (achievement.id in current.unlockedAchievementIds) return current
        val updated = current.copy(
            totalPoints = current.totalPoints + achievement.pointsReward,
            unlockedAchievementIds = current.unlockedAchievementIds + achievement.id,
            updatedAt = Instant.now()
        )
        return persist(updated)
    }

    override suspend fun addPoints(points: Long): UserStats {
        val current = try {
            getStats()
        } catch (e: Exception) {
            Log.e("UserStatsRepositoryImpl", "Error in current", e)
            throw e
        }
        return persist(current.copy(totalPoints = current.totalPoints + points))
    }

    override suspend fun recordTaskCompletion(priority: Priority): UserStats {
        val current = try {
            getStats()
        } catch (e: Exception) {
            Log.e("UserStatsRepositoryImpl", "Error in current", e)
            throw e
        }
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
        
        // Check for achievements
        val achievements = Achievements.getAchievementsForTaskCount(newCompleted)
        for (ach in achievements) {
            if (ach.id !in updated.unlockedAchievementIds) {
                updated = updated.copy(
                    totalPoints = updated.totalPoints + ach.pointsReward,
                    unlockedAchievementIds = updated.unlockedAchievementIds + ach.id
                )
            }
        }
        
        return persist(updated)
    }

    private suspend fun persist(stats: UserStats): UserStats {
        userStatsDao.upsert(stats.toEntity())
        return stats
    }
}
