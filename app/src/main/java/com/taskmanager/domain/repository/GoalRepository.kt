package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    suspend fun createGoal(goal: Goal): Long
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(id: Long)
    fun getAllGoals(): Flow<List<Goal>>
}
