package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.GoalDao
import com.taskmanager.domain.model.Goal
import com.taskmanager.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import android.util.Log

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override suspend fun createGoal(goal: Goal): Long = try {
        
    } catch (e: Exception) {
        Log.e("GoalRepositoryImpl", "Error in Long", e)
        throw e
    }
        goalDao.insert(goal.toEntity())

    override suspend fun updateGoal(goal: Goal) {
        try {
            goalDao.update(goal.toEntity())
        } catch (e: Exception) {
            Log.e("GoalRepositoryImpl", "Error in Goal)", e)
            throw e
        }
    }

    override suspend fun deleteGoal(id: Long) {
        try {
            goalDao.deleteById(id)
        } catch (e: Exception) {
            Log.e("GoalRepositoryImpl", "Error in Long)", e)
            throw e
        }
    }

    override fun getAllGoals(): Flow<List<Goal>> = try {
        
    } catch (e: Exception) {
        Log.e("GoalRepositoryImpl", "Error in Flow<List<Goal>>", e)
        throw e
    }
        goalDao.getAll().map { list -> list.map { it.toDomain() } }
}
