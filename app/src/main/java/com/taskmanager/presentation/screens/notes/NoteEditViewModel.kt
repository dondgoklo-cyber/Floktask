package com.taskmanager.presentation.screens.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Note
import com.taskmanager.domain.repository.NoteRepository
import com.taskmanager.domain.usecase.note.CreateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

data class NoteEditState(
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = true,
    val isSaved: Boolean = false
)

@HiltViewModel
class NoteEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
    private val createNoteUseCase: CreateNoteUseCase
) : ViewModel() {

    private val noteId: Long = savedStateHandle.get<Long>("noteId") ?: 0L
    val isNew: Boolean get() = noteId <= 0L

    private val _state = MutableStateFlow(NoteEditState())
    val state: StateFlow<NoteEditState> = _state.asStateFlow()

    private var saveJob: Job? = null
    private var currentNote: Note? = null
    private var hasCreated: Boolean = false

    init {
        if (!isNew) loadNote(noteId) else _state.value = NoteEditState(isLoading = false)
    }

    private fun loadNote(id: Long) {
        viewModelScope.launch {
            try {
                val note = noteRepository.getNoteById(id)
                if (note != null) {
                    currentNote = note
                    _state.value = NoteEditState(
                        title = note.title,
                        content = note.contentMarkdown,
                        isLoading = false,
                        isSaved = true
                    )
                } else {
                    _state.value = NoteEditState(isLoading = false)
                }
            } catch (e: Exception) {
                Log.e("NoteEditViewModel", "Error in launch block", e)
            }
        }
    }

    fun onTitleChange(value: String) {
        _state.value = _state.value.copy(title = value, isSaved = false)
        scheduleSave()
    }

    fun onContentChange(value: String) {
        _state.value = _state.value.copy(content = value, isSaved = false)
        scheduleSave()
    }

    private fun scheduleSave() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            try {
                delay(800)
                saveNow()
            } catch (e: Exception) {
                Log.e("NoteEditViewModel", "Error in launch block", e)
            }
        }
    }

    fun saveNow() {
        if (hasCreated && currentNote == null) return
        val s = _state.value
        viewModelScope.launch {
            try {
                if (isNew && !hasCreated) {
                    val id = createNoteUseCase(Note(
                        title = s.title,
                        contentMarkdown = s.content
                    ))
                    currentNote = Note(id = id, title = s.title, contentMarkdown = s.content)
                    hasCreated = true
                } else {
                    currentNote?.let { note ->
                        noteRepository.updateNote(note.copy(
                            title = s.title,
                            contentMarkdown = s.content
                        ))
                        currentNote = note.copy(title = s.title, contentMarkdown = s.content)
                    }
                }
                _state.value = _state.value.copy(isSaved = true)
            } catch (e: Exception) {
                Log.e("NoteEditViewModel", "Error in launch block", e)
            }
        }
    }
}
