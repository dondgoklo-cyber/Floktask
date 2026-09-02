package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.repository.TagRepository
import javax.inject.Inject
import android.util.Log

class UpdateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tag: Tag) = runCatching {
        tagRepository.updateTag(tag)
    }.onFailure { e ->
        Log.e("UpdateTagUseCase", "Error in invoke", e)
    }
}
