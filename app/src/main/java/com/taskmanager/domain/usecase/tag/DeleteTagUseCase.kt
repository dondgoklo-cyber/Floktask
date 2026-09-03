package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.repository.TagRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class DeleteTagUseCase @Inject constructor( 
    private val logger: Logger,
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(id: Long) = runCatching {
        tagRepository.deleteTag(id)
    }.onFailure { e ->
        logger.error("DeleteTagUseCase", "Error in invoke", e)
    }
}
