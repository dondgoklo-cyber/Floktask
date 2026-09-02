package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject
import android.util.Log

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(id: Long) = runCatching {
        repository.deleteNote(id)
    }.onFailure { e ->
        Log.e("DeleteNoteUseCase", "Error in invoke", e)
    }
}
