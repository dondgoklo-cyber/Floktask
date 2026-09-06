package com.taskmanager.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.model.NoteFolder
import com.taskmanager.domain.usecase.note.CreateFolderUseCase
import com.taskmanager.domain.usecase.note.CreateNoteUseCase
import com.taskmanager.domain.usecase.note.DeleteNoteUseCase
import com.taskmanager.domain.usecase.note.GetAllFoldersUseCase
import com.taskmanager.domain.usecase.note.GetAllNotesUseCase
import com.taskmanager.domain.usecase.note.SetPinnedUseCase
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
    private val getAllFoldersUseCase: GetAllFoldersUseCase,
    private val createNoteUseCase: CreateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val createFolderUseCase: CreateFolderUseCase,
    private val setPinnedUseCase: SetPinnedUseCase,
    private val logger: Logger
) : ViewModel() {

    val state: StateFlow<NotesUiState> = combine(
        getAllNotesUseCase(),
        getAllFoldersUseCase()
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
            try {
                createFolderUseCase(NoteFolder(name = name.trim()))
                closeCreateFolderDialog()
            } catch (e: Exception) {
                logger.error("NotesViewModel", "Error creating folder", e)
            }
        }
    }

    fun createNote(onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                val id = createNoteUseCase(Note(title = "", contentMarkdown = ""))
                onCreated(id)
            } catch (e: Exception) {
                logger.error("NotesViewModel", "Error creating note", e)
            }
        }
    }

    fun createNoteWithContent(title: String, content: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                val id = createNoteUseCase(Note(title = title, contentMarkdown = content))
                onCreated(id)
            } catch (e: Exception) {
                logger.error("NotesViewModel", "Error creating note with content", e)
            }
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            try {
                setPinnedUseCase(note.id ?: 0, !note.pinned)
            } catch (e: Exception) {
                logger.error("NotesViewModel", "Error toggling pin", e)
            }
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            try {
                deleteNoteUseCase(id)
            } catch (e: Exception) {
                logger.error("NotesViewModel", "Error deleting note", e)
            }
        }
    }
}
