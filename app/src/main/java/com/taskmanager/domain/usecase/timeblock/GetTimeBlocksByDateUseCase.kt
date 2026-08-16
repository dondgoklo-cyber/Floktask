package com.taskmanager.domain.usecase.timeblock

import com.taskmanager.domain.model.TimeBlock
import com.taskmanager.domain.repository.TimeBlockRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetTimeBlocksByDateUseCase @Inject constructor(
    private val timeBlockRepository: TimeBlockRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<TimeBlock>> =
        timeBlockRepository.getTimeBlocksByDate(date)
}
