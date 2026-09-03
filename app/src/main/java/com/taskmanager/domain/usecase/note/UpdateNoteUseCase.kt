package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.model.Note
import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject
import com.taskmanager.domain.logger.Logger

class UpdateNoteUseCase @Inject constructor( 
    private val logger: Logger,
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) = runCatching {
        repository.updateNote(note)
    }.onFailure { e ->
        logger.error("UpdateNoteUseCase", "Error in invoke", e)
    }
}
