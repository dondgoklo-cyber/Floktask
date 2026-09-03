package com.taskmanager.domain.usecase.gamification

import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.UserStats
import com.taskmanager.domain.repository.UserStatsRepository
import javax.inject.Inject
import android.util.Log

class RecordTaskCompletionUseCase @Inject constructor(
    private val repository: UserStatsRepository
) {
    suspend operator fun invoke(priority: Priority): UserStats = runCatching {
        repository.recordTaskCompletion(priority)
    }.onFailure { e ->
        Log.e("RecordTaskCompletionUseCase", "Error in invoke", e)
    }.getOrThrow()
}
