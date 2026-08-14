package com.taskmanager.presentation.screens.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.domain.repository.TaskRepository
import com.taskmanager.domain.usecase.pomodoro.GetPomodoroStatsUseCase
import com.taskmanager.domain.usecase.pomodoro.PomodoroStats
import com.taskmanager.domain.usecase.pomodoro.SavePomodoroSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FocusUiState(
    val type: PomodoroType = PomodoroType.WORK,
    val durationMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val taskId: Long? = null,
    val taskTitle: String? = null,
    val completedPomodoros: Int = 0,
    val stats: PomodoroStats? = null
)

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val savePomodoroSessionUseCase: SavePomodoroSessionUseCase,
    private val getPomodoroStatsUseCase: GetPomodoroStatsUseCase,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FocusUiState())
    val state: StateFlow<FocusUiState> = _state.asStateFlow()

    private var timerJob: Job? = null

    // Настройки (по умолчанию 25/5/15)
    private var workDuration = 25
    private var shortBreak = 5
    private var longBreak = 15

    init {
        observeStats()
    }

    private fun observeStats() {
        viewModelScope.launch {
            getPomodoroStatsUseCase().collect { stats ->
                _state.value = _state.value.copy(stats = stats)
            }
        }
    }

    fun setTask(taskId: Long?) {
        _state.value = _state.value.copy(taskId = taskId, taskTitle = null)
        if (taskId != null) {
            viewModelScope.launch {
                val title = taskRepository.getTaskById(taskId)?.title
                _state.value = _state.value.copy(taskTitle = title)
            }
        }
    }

    fun start() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = true)
        timerJob = viewModelScope.launch {
            while (_state.value.remainingSeconds > 0) {
                delay(1000)
                _state.value = _state.value.copy(
                    remainingSeconds = _state.value.remainingSeconds - 1
                )
            }
            onComplete()
        }
    }

    fun pause() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun reset() {
        timerJob?.cancel()
        val duration = when (_state.value.type) {
            PomodoroType.WORK -> workDuration
            PomodoroType.SHORT_BREAK -> shortBreak
            PomodoroType.LONG_BREAK -> longBreak
        }
        _state.value = _state.value.copy(
            isRunning = false,
            remainingSeconds = duration * 60,
            totalSeconds = duration * 60,
            durationMinutes = duration
        )
    }

    fun selectType(type: PomodoroType) {
        timerJob?.cancel()
        val duration = when (type) {
            PomodoroType.WORK -> workDuration
            PomodoroType.SHORT_BREAK -> shortBreak
            PomodoroType.LONG_BREAK -> longBreak
        }
        _state.value = _state.value.copy(
            type = type,
            isRunning = false,
            remainingSeconds = duration * 60,
            totalSeconds = duration * 60,
            durationMinutes = duration
        )
    }

    private fun onComplete() {
        viewModelScope.launch {
            // Сохраняем сессию
            savePomodoroSessionUseCase(
                taskId = _state.value.taskId,
                durationMinutes = _state.value.durationMinutes,
                type = _state.value.type
            )

            if (_state.value.type == PomodoroType.WORK) {
                _state.value = _state.value.copy(
                    completedPomodoros = _state.value.completedPomodoros + 1
                )
                // После работы — перерыв (длинный каждый 4-й)
                val nextType = if (_state.value.completedPomodoros % 4 == 0) {
                    PomodoroType.LONG_BREAK
                } else {
                    PomodoroType.SHORT_BREAK
                }
                selectType(nextType)
            } else {
                // После перерыва — работа
                selectType(PomodoroType.WORK)
            }
        }
    }
}
