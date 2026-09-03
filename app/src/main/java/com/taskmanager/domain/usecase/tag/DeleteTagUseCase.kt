package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.TagRepository
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Long) = runCatching {
        tagRepository.deleteTag(id)
    }.onFailure { e ->
        logger.error("DeleteTagUseCase", "Error in invoke", e)
    }
}
