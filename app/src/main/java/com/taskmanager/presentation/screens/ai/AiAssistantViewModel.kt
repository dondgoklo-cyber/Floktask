package com.taskmanager.presentation.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.ai.AiSuggestion
import com.taskmanager.domain.usecase.ai.GetAiSuggestionsUseCase
import com.taskmanager.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiAssistantViewModel @Inject constructor(
    getAiSuggestionsUseCase: GetAiSuggestionsUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AiState>(AiState.Loading)
    val state: StateFlow<AiState> = _state.asStateFlow()

    init {
        getAiSuggestionsUseCase()
            .onEach { _state.value = AiState.Success(it) }
            .catch { cause -> _state.value = AiState.Error(cause.message ?: "Unknown error") }
            .launchIn(viewModelScope)
    }

    fun applySuggestion(task: Task, priority: Priority) {
        viewModelScope.launch {
            updateTaskUseCase(task.copy(priority = priority))
        }
    }
}

sealed class AiState {
    data object Loading : AiState()
    data class Success(val suggestions: List<AiSuggestion>) : AiState()
    data class Error(val message: String) : AiState()
}
