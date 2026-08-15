package com.taskmanager.domain.repository

import com.taskmanager.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun upsertBudget(budget: Budget): Long
    suspend fun deleteBudget(id: Long)
    fun getAllBudgets(): Flow<List<Budget>>
    suspend fun getBudgetByCategory(categoryId: Long): Budget?
}
