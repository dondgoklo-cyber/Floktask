package com.taskmanager.domain.usecase.finance

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Goal
import com.taskmanager.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all goals.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetAllGoalsUseCase @Inject constructor(
    private val goalRepository: GoalRepository,
    private val logger: Logger
) {
    operator fun invoke(): Flow<List<Goal>> = goalRepository.getAllGoals()
}
