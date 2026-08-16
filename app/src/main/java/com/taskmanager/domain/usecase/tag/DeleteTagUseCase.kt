package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.repository.TagRepository
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(id: Long) {
        tagRepository.deleteTag(id)
    }
}
