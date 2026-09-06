package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Goal
import com.taskmanager.domain.repository.GoalRepository
import javax.inject.Inject

/**
 * Use case for creating a goal.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class CreateGoalUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(goal: Goal): Long = runCatching {
        goalRepository.createGoal(goal)
    }.onFailure { e ->
        logger.error("CreateGoalUseCase", "Error creating goal", e)
    }.getOrThrow()
}
