package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.repository.TagRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class UpdateTagUseCase @Inject constructor( 
    private val logger: Logger,
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tag: Tag) = runCatching {
        tagRepository.updateTag(tag)
    }.onFailure { e ->
        logger.error("UpdateTagUseCase", "Error in invoke", e)
    }
}
