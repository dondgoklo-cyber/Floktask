package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(note: Note) = runCatching {
        repository.updateNote(note)
    }.onFailure { e ->
        logger.error("UpdateNoteUseCase", "Error in invoke", e)
    }
}
