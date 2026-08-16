package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.repository.TagRepository
import javax.inject.Inject

class SetTagsForTaskUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(taskId: Long, tagNames: List<String>): List<Long> =
        tagRepository.setTagsForTask(taskId, tagNames)
}
