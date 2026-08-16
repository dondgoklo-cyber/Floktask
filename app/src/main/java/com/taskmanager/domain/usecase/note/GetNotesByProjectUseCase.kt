package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.model.Note
import com.taskmanager.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesByProjectUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(projectId: Long): Flow<List<Note>> = repository.getNotesByProject(projectId)
}
