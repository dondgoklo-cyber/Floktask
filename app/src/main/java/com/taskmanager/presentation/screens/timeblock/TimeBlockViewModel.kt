package com.taskmanager.presentation.screens.timeblock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.TimeBlock
import com.taskmanager.domain.usecase.timeblock.CreateTimeBlockUseCase
import com.taskmanager.domain.usecase.timeblock.DeleteTimeBlockUseCase
import com.taskmanager.domain.usecase.timeblock.GetTimeBlocksByDateUseCase
import com.taskmanager.domain.usecase.timeblock.UpdateTimeBlockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi

enum class TimeBlockViewMode { DAY, WEEK }

data class TimeBlockUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val viewMode: TimeBlockViewMode = TimeBlockViewMode.DAY,
    val blocks: List<TimeBlock> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimeBlockViewModel @Inject constructor(
    private val getTimeBlocksByDateUseCase: GetTimeBlocksByDateUseCase,
    private val createTimeBlockUseCase: CreateTimeBlockUseCase,
    private val updateTimeBlockUseCase: UpdateTimeBlockUseCase,
    private val deleteTimeBlockUseCase: DeleteTimeBlockUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _viewMode = MutableStateFlow(TimeBlockViewMode.DAY)
    val viewMode: StateFlow<TimeBlockViewMode> = _viewMode.asStateFlow()

    val blocks: StateFlow<List<TimeBlock>> = _selectedDate
        .flatMapLatest { date -> getTimeBlocksByDateUseCase(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun previousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
    }

    fun nextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    fun goToToday() {
        _selectedDate.value = LocalDate.now()
    }

    fun setViewMode(mode: TimeBlockViewMode) {
        _viewMode.value = mode
    }

    fun createBlock(
        title: String,
        start: Instant,
        end: Instant,
        taskId: Long? = null,
        projectId: Long? = null,
        color: String? = null
    ) {
        viewModelScope.launch {
            runCatching {
                createTimeBlockUseCase(
                    TimeBlock(
                        title = title,
                        taskId = taskId,
                        projectId = projectId,
                        startTime = start,
                        endTime = end,
                        color = color
                    )
                )
            }
        }
    }

    fun updateBlock(block: TimeBlock) {
        viewModelScope.launch {
            runCatching { updateTimeBlockUseCase(block.copy(updatedAt = Instant.now())) }
        }
    }

    fun moveBlock(block: TimeBlock, newStart: Instant) {
        val durationMillis = block.endTime.toEpochMilli() - block.startTime.toEpochMilli()
        val newEnd = Instant.ofEpochMilli(newStart.toEpochMilli() + durationMillis)
        updateBlock(block.copy(startTime = newStart, endTime = newEnd))
    }

    fun toggleCompleted(block: TimeBlock) {
        updateBlock(block.copy(isCompleted = !block.isCompleted))
    }

    fun deleteBlock(id: Long) {
        viewModelScope.launch { deleteTimeBlockUseCase(id) }
    }

    /**
     * Detects whether [start]-[end] overlaps any existing block on the selected date.
     */
    fun hasConflict(start: Instant, end: Instant): Boolean {
        return blocks.value.any { existing ->
            start < existing.endTime && end > existing.startTime
        }
    }
}
