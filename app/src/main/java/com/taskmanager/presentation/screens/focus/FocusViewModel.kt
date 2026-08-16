package com.taskmanager.presentation.screens.focus

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.domain.repository.TaskRepository
import com.taskmanager.domain.usecase.pomodoro.GetPomodoroStatsUseCase
import com.taskmanager.domain.usecase.pomodoro.PomodoroStats
import com.taskmanager.domain.usecase.pomodoro.SavePomodoroSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    val stats: PomodoroStats? = null,
    val isServiceRunning: Boolean = false
)

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val savePomodoroSessionUseCase: SavePomodoroSessionUseCase,
    private val getPomodoroStatsUseCase: GetPomodoroStatsUseCase,
    private val taskRepository: TaskRepository,
    @ApplicationContext private val context: Context
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
        restoreTimerState()  // ✅ Восстановление после Process Death
    }

    private fun observeStats() {
        viewModelScope.launch {
            getPomodoroStatsUseCase().collect { stats ->
                _state.value = _state.value.copy(stats = stats)
            }
        }
    }

    /**
     * Восстановление состояния таймера после Process Death
     */
    private fun restoreTimerState() {
        val prefs = context.getSharedPreferences(
            PomodoroService.PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val isActive = prefs.getBoolean(PomodoroService.KEY_IS_ACTIVE, false)

        if (isActive) {
            val endTime = prefs.getLong(PomodoroService.KEY_END_TIME, 0L)
            val taskId = prefs.getLong(PomodoroService.KEY_TASK_ID, -1L)
            val duration = prefs.getInt(PomodoroService.KEY_DURATION, 25)
            val typeString = prefs.getString(PomodoroService.KEY_TYPE, "WORK") ?: "WORK"

            val type = when (typeString) {
                "WORK" -> PomodoroType.WORK
                "SHORT_BREAK" -> PomodoroType.SHORT_BREAK
                "LONG_BREAK" -> PomodoroType.LONG_BREAK
                else -> PomodoroType.WORK
            }

            if (endTime > System.currentTimeMillis()) {
                // Таймер еще активен - восстанавливаем
                val remaining = ((endTime - System.currentTimeMillis()) / 1000).toInt()

                _state.value = _state.value.copy(
                    isRunning = true,
                    isServiceRunning = true,
                    remainingSeconds = remaining,
                    totalSeconds = duration * 60,
                    durationMinutes = duration,
                    type = type,
                    taskId = if (taskId == -1L) null else taskId
                )

                // Загружаем название задачи
                if (taskId != -1L) {
                    loadTaskTitle(taskId)
                }

                // Запускаем наблюдение за таймером
                startObservingTimer(endTime)
            } else {
                // Таймер истек - очищаем
                clearTimerState()
            }
        }
    }

    /**
     * Наблюдение за таймером (обновление UI каждую секунду)
     */
    private fun startObservingTimer(endTime: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val remaining = ((endTime - System.currentTimeMillis()) / 1000).toInt()
                if (remaining > 0) {
                    _state.value = _state.value.copy(remainingSeconds = remaining)
                    delay(1000)
                } else {
                    // Таймер завершен - Service уже сохранил сессию
                    _state.value = _state.value.copy(
                        isRunning = false,
                        isServiceRunning = false
                    )
                    clearTimerState()
                    break
                }
            }
        }
    }

    private fun loadTaskTitle(taskId: Long) {
        viewModelScope.launch {
            val title = taskRepository.getTaskById(taskId)?.title
            _state.value = _state.value.copy(taskTitle = title)
        }
    }

    private fun clearTimerState() {
        context.getSharedPreferences(PomodoroService.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(PomodoroService.KEY_IS_ACTIVE, false)
            .apply()
    }

    fun setTask(taskId: Long?) {
        _state.value = _state.value.copy(taskId = taskId, taskTitle = null)
        if (taskId != null) {
            loadTaskTitle(taskId)
        }
    }

    /**
     * Запуск Pomodoro через Foreground Service
     */
    fun start() {
        if (_state.value.isRunning) return

        val duration = _state.value.durationMinutes
        val taskId = _state.value.taskId ?: -1L
        val type = _state.value.type.name

        // Запускаем Foreground Service
        val intent = Intent(context, PomodoroService::class.java).apply {
            putExtra(PomodoroService.EXTRA_TASK_ID, taskId)
            putExtra(PomodoroService.EXTRA_DURATION, duration)
            putExtra(PomodoroService.EXTRA_TYPE, type)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        // Обновляем состояние
        val endTime = System.currentTimeMillis() + duration * 60 * 1000
        _state.value = _state.value.copy(
            isRunning = true,
            isServiceRunning = true,
            totalSeconds = duration * 60,
            remainingSeconds = duration * 60
        )

        // Наблюдаем за таймером
        startObservingTimer(endTime)
    }

    /**
     * Пауза таймера
     */
    fun pause() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)

        // Останавливаем Service
        context.stopService(Intent(context, PomodoroService::class.java))
        clearTimerState()
    }

    /**
     * Сброс таймера
     */
    fun reset() {
        timerJob?.cancel()

        // Останавливаем Service если запущен
        context.stopService(Intent(context, PomodoroService::class.java))
        clearTimerState()

        val duration = when (_state.value.type) {
            PomodoroType.WORK -> workDuration
            PomodoroType.SHORT_BREAK -> shortBreak
            PomodoroType.LONG_BREAK -> longBreak
        }

        _state.value = _state.value.copy(
            isRunning = false,
            isServiceRunning = false,
            remainingSeconds = duration * 60,
            totalSeconds = duration * 60,
            durationMinutes = duration
        )
    }

    /**
     * Выбор типа Pomodoro
     */
    fun selectType(type: PomodoroType) {
        timerJob?.cancel()

        // Останавливаем текущий Service если запущен
        if (_state.value.isServiceRunning) {
            context.stopService(Intent(context, PomodoroService::class.java))
            clearTimerState()
        }

        val duration = when (type) {
            PomodoroType.WORK -> workDuration
            PomodoroType.SHORT_BREAK -> shortBreak
            PomodoroType.LONG_BREAK -> longBreak
        }

        _state.value = _state.value.copy(
            type = type,
            isRunning = false,
            isServiceRunning = false,
            remainingSeconds = duration * 60,
            totalSeconds = duration * 60,
            durationMinutes = duration
        )
    }

    /**
     * Настройка длительности (опционально)
     */
    fun setDurations(work: Int, short: Int, long: Int) {
        workDuration = work
        shortBreak = short
        longBreak = long

        // Если таймер не запущен - обновляем текущую длительность
        if (!_state.value.isRunning) {
            val duration = when (_state.value.type) {
                PomodoroType.WORK -> workDuration
                PomodoroType.SHORT_BREAK -> shortBreak
                PomodoroType.LONG_BREAK -> longBreak
            }
            _state.value = _state.value.copy(
                durationMinutes = duration,
                remainingSeconds = duration * 60,
                totalSeconds = duration * 60
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
