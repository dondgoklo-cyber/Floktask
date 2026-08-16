package com.taskmanager.domain.usecase.gamification

import com.taskmanager.domain.model.UserStats
import com.taskmanager.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUserStatsUseCase @Inject constructor(
    private val repository: UserStatsRepository
) {
    operator fun invoke(): Flow<UserStats?> = repository.observeStats()
}
