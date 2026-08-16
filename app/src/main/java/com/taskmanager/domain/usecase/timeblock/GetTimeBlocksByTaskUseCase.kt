package com.taskmanager.domain.usecase.timeblock

import com.taskmanager.domain.model.TimeBlock
import com.taskmanager.domain.repository.TimeBlockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTimeBlocksByTaskUseCase @Inject constructor(
    private val timeBlockRepository: TimeBlockRepository
) {
    operator fun invoke(taskId: Long): Flow<List<TimeBlock>> =
        timeBlockRepository.getTimeBlocksByTask(taskId)
}
