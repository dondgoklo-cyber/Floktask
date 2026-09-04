package com.taskmanager.data.repository
import com.taskmanager.domain.logger.Logger

import com.taskmanager.data.local.dao.BudgetDao
import com.taskmanager.domain.model.Budget
import com.taskmanager.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val logger: Logger,
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override suspend fun upsertBudget(budget: Budget): Long = try {
        budgetDao.insert(budget.toEntity())
    } catch (e: Exception) {
        logger.error("BudgetRepositoryImpl", "Error in Long", e)
        throw e
    }

    override suspend fun deleteBudget(id: Long) {
        try {
            budgetDao.deleteById(id)
        } catch (e: Exception) {
            logger.error("BudgetRepositoryImpl", "Error in Long", e)
            throw e
        }
    }

    override fun getAllBudgets(): Flow<List<Budget>> = try {
        budgetDao.getAll().map { list -> list.map { it.toDomain() } }
    } catch (e: Exception) {
        logger.error("BudgetRepositoryImpl", "Error in Flow<List<Budget>>", e)
        throw e
    }

    override suspend fun getBudgetByCategory(categoryId: Long): Budget? = try {
        budgetDao.getByCategory(categoryId)?.toDomain()
    } catch (e: Exception) {
        logger.error("BudgetRepositoryImpl", "Error in Budget?", e)
        throw e
    }
}
