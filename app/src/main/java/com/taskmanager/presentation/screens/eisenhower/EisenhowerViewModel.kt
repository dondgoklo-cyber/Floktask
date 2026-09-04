package com.taskmanager.presentation
import com.taskmanager.domain.logger.Logger.screens.eisenhower

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.EisenhowerQuadrant
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.eisenhower.GetEisenhowerTasksUseCase
import com.taskmanager.domain.usecase.eisenhower.UpdateEisenhowerQuadrantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EisenhowerUiState(
    val quadrants: Map<EisenhowerQuadrant, List<Task>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class EisenhowerViewModel @Inject constructor(
    getEisenhowerTasksUseCase: GetEisenhowerTasksUseCase,
    private val updateEisenhowerQuadrantUseCase: UpdateEisenhowerQuadrantUseCase
) : ViewModel() {

    val state: StateFlow<EisenhowerUiState> = getEisenhowerTasksUseCase()
        .map { quadrants -> EisenhowerUiState(quadrants = quadrants, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.Lazily, EisenhowerUiState(isLoading = true))

    fun moveTask(taskId: Long, quadrant: EisenhowerQuadrant) {
        viewModelScope.launch {
        try {
            updateEisenhowerQuadrantUseCase(taskId, quadrant)
        } catch (e: Exception) {
            logger.error("EisenhowerViewModel", "Error in launch block", e)
            // Optionally update state to show error
        }
    }
    }

    fun clearQuadrant(taskId: Long) {
        viewModelScope.launch {
        try {
            updateEisenhowerQuadrantUseCase(taskId, null)
        } catch (e: Exception) {
            logger.error("EisenhowerViewModel", "Error in launch block", e)
            // Optionally update state to show error
        }
    }
    }
}
