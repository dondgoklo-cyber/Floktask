package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.model.Note
import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject
import android.util.Log

class CreateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Long = runCatching {
        repository.createNote(note)
    }.onFailure { e ->
        Log.e("CreateNoteUseCase", "Error in invoke", e)
    }.getOrThrow()
}
