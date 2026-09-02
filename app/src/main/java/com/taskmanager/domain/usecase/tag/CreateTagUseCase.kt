package com.taskmanager.domain.usecase.tag

import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.repository.TagRepository
import javax.inject.Inject
import android.util.Log

class CreateTagUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(tag: Tag): Long = runCatching {
        
    }.onFailure { e ->
        Log.e("CreateTagUseCase", "Error in invoke", e)
    }
        tagRepository.createTag(tag)
}
