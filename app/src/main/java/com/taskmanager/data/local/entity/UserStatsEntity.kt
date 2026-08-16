package com.taskmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Long = 1,
    val totalPoints: Long = 0,
    val level: Int = 1,
    val completedTasks: Int = 0,
    val streak: Int = 0,
    val unlockedAchievements: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
