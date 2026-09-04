package com.taskmanager.data.repository
import com.taskmanager.domain.logger.Logger

import com.taskmanager.data.local.dao.GoalDao
import com.taskmanager.domain.model.Goal
import com.taskmanager.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val goalDao: GoalDao
) : GoalRepository {

    override suspend fun createGoal(goal: Goal): Long = try {
        goalDao.insert(goal.toEntity())
    } catch (e: Exception) {
        logger.error("GoalRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun updateGoal(goal: Goal) {
        try {
            goalDao.update(goal.toEntity())
        } catch (e: Exception) {
            logger.error("GoalRepositoryImpl", "Error in Goal", e)
            throw e
        }
    }

    override suspend fun deleteGoal(id: Long) {
        try {
            goalDao.deleteById(id)
        } catch (e: Exception) {
            logger.error("GoalRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override fun getAllGoals(): Flow<List<Goal>> = try {
        goalDao.getAll().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        logger.error("GoalRepositoryImpl", "Error in Flow<List<Goal>>", e)
        throw e
    }
}
