package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.GoalRepository
import javax.inject.Inject

/**
 * Use case for deleting a goal.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class DeleteGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Long) = runCatching {
        goalRepository.deleteGoal(id)
    }.onFailure { e ->
        logger.error("DeleteGoalUseCase", "Error deleting goal", e)
    }
}
