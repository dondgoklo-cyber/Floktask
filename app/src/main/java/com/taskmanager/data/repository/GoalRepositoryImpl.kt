package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.GoalDao
import com.taskmanager.domain.model.Goal
import com.taskmanager.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override suspend fun createGoal(goal: Goal): Long =
        goalDao.insert(goal.toEntity())

    override suspend fun updateGoal(goal: Goal) {
        goalDao.update(goal.toEntity())
    }

    override suspend fun deleteGoal(id: Long) {
        goalDao.deleteById(id)
    }

    override fun getAllGoals(): Flow<List<Goal>> =
        goalDao.getAll().map { list -> list.map { it.toDomain() } }
}
