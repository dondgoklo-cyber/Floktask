package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving all tags.
 * Part of Clean Architecture - Presentation layer depends only on UseCases, not Repositories.
 */
class GetAllTagsUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val logger: Logger
) {
    operator fun invoke(): Flow<List<Tag>> = tagRepository.getAllTags()
}
