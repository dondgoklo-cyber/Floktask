package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.model.Note
import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject
import android.util.Log

class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) = runCatching {
        repository.updateNote(note)
    }.onFailure { e ->
        Log.e("UpdateNoteUseCase", "Error in invoke", e)
    }
}
