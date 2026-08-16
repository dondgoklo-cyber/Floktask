package com.taskmanager.domain.usecase.timeblock

import com.taskmanager.domain.model.TimeBlock
import com.taskmanager.domain.repository.TimeBlockRepository
import javax.inject.Inject

class CreateTimeBlockUseCase @Inject constructor(
    private val timeBlockRepository: TimeBlockRepository
) {
    suspend operator fun invoke(block: TimeBlock): Long =
        timeBlockRepository.createTimeBlock(block)
}
