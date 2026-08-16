package com.taskmanager.data.repository

import com.taskmanager.data.local.dao.BudgetDao
import com.taskmanager.domain.model.Budget
import com.taskmanager.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override suspend fun upsertBudget(budget: Budget): Long =
        budgetDao.insert(budget.toEntity())

    override suspend fun deleteBudget(id: Long) {
        budgetDao.deleteById(id)
    }

    override fun getAllBudgets(): Flow<List<Budget>> =
        budgetDao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getBudgetByCategory(categoryId: Long): Budget? =
        budgetDao.getByCategory(categoryId)?.toDomain()
}
