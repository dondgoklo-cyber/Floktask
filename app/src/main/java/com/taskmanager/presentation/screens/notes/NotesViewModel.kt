package com.taskmanager.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.model.NoteFolder
import com.taskmanager.domain.repository.NoteFolderRepository
import com.taskmanager.domain.repository.NoteRepository
import com.taskmanager.domain.usecase.note.CreateNoteUseCase
import com.taskmanager.domain.usecase.note.DeleteNoteUseCase
import com.taskmanager.domain.usecase.note.GetAllNotesUseCase
import com.taskmanager.util.HapticManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotesUiState(
    val pinnedNotes: List<Note> = emptyList(),
    val recentNotes: List<Note> = emptyList(),
    val folders: List<NoteFolder> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    getAllNotesUseCase: GetAllNotesUseCase,
    private val noteRepository: NoteRepository,
    private val noteFolderRepository: NoteFolderRepository,
    private val createNoteUseCase: CreateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    val hapticManager: HapticManager
) : ViewModel() {

    val state: StateFlow<NotesUiState> = combine(
        getAllNotesUseCase(),
        noteFolderRepository.getAllFolders()
    ) { notes, folders ->
        NotesUiState(
            pinnedNotes = notes.filter { it.pinned },
            recentNotes = notes.filter { !it.pinned }.take(20),
            folders = folders,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, NotesUiState())

    private val _showCreateFolder = MutableStateFlow(false)
    val showCreateFolder: StateFlow<Boolean> = _showCreateFolder.asStateFlow()

    fun openCreateFolderDialog() { _showCreateFolder.value = true }
    fun closeCreateFolderDialog() { _showCreateFolder.value = false }

    fun createFolder(name: String) {
        viewModelScope.launch {
            noteFolderRepository.createFolder(NoteFolder(name = name.trim()))
            closeCreateFolderDialog()
        }
    }

    fun createNote(onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = createNoteUseCase(Note(title = "", contentMarkdown = ""))
            onCreated(id)
        }
    }

    fun createNoteWithContent(title: String, content: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = createNoteUseCase(Note(title = title, contentMarkdown = content))
            onCreated(id)
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            noteRepository.setPinned(note.id ?: 0, !note.pinned)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            deleteNoteUseCase(id)
        }
    }
}
