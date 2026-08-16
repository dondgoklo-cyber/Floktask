package com.taskmanager.domain.usecase.timeblock

import com.taskmanager.domain.repository.TimeBlockRepository
import javax.inject.Inject

class DeleteTimeBlockUseCase @Inject constructor(
    private val timeBlockRepository: TimeBlockRepository
) {
    suspend operator fun invoke(id: Long) {
        timeBlockRepository.deleteTimeBlock(id)
    }
}
