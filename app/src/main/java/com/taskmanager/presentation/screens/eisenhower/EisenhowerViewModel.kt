package com.taskmanager.presentation.screens.eisenhower

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.eisenhower.DistributeTasksByEisenhowerUseCase
import com.taskmanager.domain.usecase.eisenhower.GetSmartAssistantSuggestionUseCase
import com.taskmanager.domain.usecase.eisenhower.Suggestion
import com.taskmanager.domain.usecase.task.GetAllTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class EisenhowerUiState(
    val buckets: Map<EisenhowerQuadrant, List<Task>> = emptyMap(),
    val suggestion: Suggestion? = null
)

@HiltViewModel
class EisenhowerViewModel @Inject constructor(
    getAllTasksUseCase: GetAllTasksUseCase,
    private val distributeTasksByEisenhowerUseCase: DistributeTasksByEisenhowerUseCase,
    private val getSmartAssistantSuggestionUseCase: GetSmartAssistantSuggestionUseCase
) : ViewModel() {

    val state: StateFlow<EisenhowerUiState> = getAllTasksUseCase()
        .map { tasks ->
            EisenhowerUiState(
                buckets = distributeTasksByEisenhowerUseCase(tasks),
                suggestion = getSmartAssistantSuggestionUseCase(tasks)
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EisenhowerUiState())
}
