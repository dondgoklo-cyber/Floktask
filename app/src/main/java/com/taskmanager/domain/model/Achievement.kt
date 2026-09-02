package com.taskmanager.domain.model

import java.time.Instant

data class UserStats(
    val id: Long = 1,
    val totalPoints: Long = 0,
    val level: Int = 1,
    val completedTasks: Int = 0,
    val streak: Int = 0,
    val unlockedAchievementIds: List<String> = emptyList(),
    val updatedAt: Instant = Instant.now()
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val pointsReward: Long,
    val icon: String? = null
)

object Achievements {
    val FIRST_TASK = Achievement("first_task", "First Step", "Complete your first task", 10)
    val TEN_TASKS = Achievement("ten_tasks", "Getting Started", "Complete 10 tasks", 50)
    val FIFTY_TASKS = Achievement("fifty_tasks", "Productive", "Complete 50 tasks", 200)
    val STREAK_3 = Achievement("streak_3", "On Fire", "3-day streak", 30)
    val STREAK_7 = Achievement("streak_7", "Unstoppable", "7-day streak", 100)
    val HIGH_PRIORITY = Achievement("high_priority", "Focused", "Complete a high-priority task", 20)

    val all: List<Achievement> = listOf(
        FIRST_TASK, TEN_TASKS, FIFTY_TASKS, STREAK_3, STREAK_7, HIGH_PRIORITY
    )

    fun byId(id: String): Achievement? = all.firstOrNull { it.id == id }

    /** Points needed to reach a given level (cumulative). */
    private fun pointsForLevel(level: Int): Long = when {
        level <= 1 -> 0
        else -> 100L * (level - 1) * level / 2
    }

    private fun levelFromPoints(points: Long): Int {
        var level = 1
        while (points >= pointsForLevel(level + 1)) level++
        return level
    }
}
