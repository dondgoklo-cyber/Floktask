package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.repository.TagRepository
import javax.inject.Inject
import android.util.Log

class DeleteTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(id: Long) = runCatching {
        tagRepository.deleteTag(id)
    }.onFailure { e ->
        Log.e("DeleteTagUseCase", "Error in invoke", e)
    }
}
