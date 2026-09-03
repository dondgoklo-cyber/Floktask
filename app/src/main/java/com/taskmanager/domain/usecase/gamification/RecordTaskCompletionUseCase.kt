package com.taskmanager.domain.usecase.gamification

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.UserStats
import com.taskmanager.domain.repository.UserStatsRepository
import javax.inject.Inject

class RecordTaskCompletionUseCase @Inject constructor(
    private val repository: UserStatsRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(priority: Priority): UserStats = runCatching {
        repository.recordTaskCompletion(priority)
    }.onFailure { e ->
        logger.error("RecordTaskCompletionUseCase", "Error in invoke", e)
    }.getOrThrow()
}
