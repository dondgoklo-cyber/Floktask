package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.repository.TagRepository
import javax.inject.Inject

class CreateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(tag: Tag): Long = runCatching {
        tagRepository.createTag(tag)
    }.onFailure { e ->
        logger.error("CreateTagUseCase", "Error in invoke", e)
    }.getOrThrow()
}
