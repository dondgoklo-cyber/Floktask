package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.BudgetRepository
import javax.inject.Inject

/**
 * Use case for deleting a budget.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class DeleteBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Long) = runCatching {
        budgetRepository.deleteBudget(id)
    }.onFailure { e ->
        logger.error("DeleteBudgetUseCase", "Error deleting budget", e)
    }
}
