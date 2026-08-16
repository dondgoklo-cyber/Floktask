package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksByTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    operator fun invoke(tagName: String): Flow<List<Task>> =
        tagRepository.getTasksByTag(tagName)
}
