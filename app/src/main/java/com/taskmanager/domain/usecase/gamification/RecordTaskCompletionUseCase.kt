package com.taskmanager.domain.usecase.gamification

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.UserStats
import com.taskmanager.domain.repository.UserStatsRepository
import javax.inject.Inject

class RecordTaskCompletionUseCase @Inject constructor(
    private val repository: UserStatsRepository
) {
    suspend operator fun invoke(priority: Priority): UserStats =
        repository.recordTaskCompletion(priority)
}
