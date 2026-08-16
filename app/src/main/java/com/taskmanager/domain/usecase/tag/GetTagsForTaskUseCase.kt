package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTagsForTaskUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    operator fun invoke(taskId: Long): Flow<List<Tag>> =
        tagRepository.getTagsForTask(taskId)
}
