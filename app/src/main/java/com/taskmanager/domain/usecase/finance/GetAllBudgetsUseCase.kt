package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Budget
import com.taskmanager.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all budgets.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetAllBudgetsUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val logger: Logger
) {
    operator fun invoke(): Flow<List<Budget>> = budgetRepository.getAllBudgets()
}
