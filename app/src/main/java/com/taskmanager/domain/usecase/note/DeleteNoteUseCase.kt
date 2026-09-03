package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class DeleteNoteUseCase @Inject constructor( 
    private val logger: Logger,
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long) = runCatching {
        repository.deleteNote(id)
    }.onFailure { e ->
        logger.error("DeleteNoteUseCase", "Error in invoke", e)
    }
}
