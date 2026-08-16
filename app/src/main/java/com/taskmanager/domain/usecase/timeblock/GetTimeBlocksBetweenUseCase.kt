package com.taskmanager.domain.usecase.timeblock

import com.taskmanager.domain.model.TimeBlock
import com.taskmanager.domain.repository.TimeBlockRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTimeBlocksBetweenUseCase @Inject constructor(
    private val timeBlockRepository: TimeBlockRepository
) {
    operator fun invoke(startEpochMillis: Long, endEpochMillis: Long): Flow<List<TimeBlock>> =
        timeBlockRepository.getTimeBlocksBetween(startEpochMillis, endEpochMillis)
}
