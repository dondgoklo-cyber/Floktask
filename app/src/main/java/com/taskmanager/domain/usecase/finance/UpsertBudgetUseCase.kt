package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Budget
import com.taskmanager.domain.repository.BudgetRepository
import javax.inject.Inject

/**
 * Use case for upserting a budget.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class UpsertBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(budget: Budget) = runCatching {
        budgetRepository.upsertBudget(budget)
    }.onFailure { e ->
        logger.error("UpsertBudgetUseCase", "Error upserting budget", e)
    }
}
