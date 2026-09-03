package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.repository.TagRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class CreateTagUseCase @Inject constructor( 
    private val logger: Logger,
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tag: Tag): Long = runCatching {
        tagRepository.createTag(tag)
    }.onFailure { e ->
        logger.error("CreateTagUseCase", "Error in invoke", e)
    }.getOrThrow()
}
