package com.taskmanager.domain.usecase.note

import com.taskmanager.domain.repository.NoteRepository
import javax.inject.Inject

class SearchNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(query: String) = repository.searchNotes(query)
}
