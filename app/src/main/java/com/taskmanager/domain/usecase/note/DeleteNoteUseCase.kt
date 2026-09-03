package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Long) = runCatching {
        repository.deleteNote(id)
    }.onFailure { e ->
        logger.error("DeleteNoteUseCase", "Error in invoke", e)
    }
}
