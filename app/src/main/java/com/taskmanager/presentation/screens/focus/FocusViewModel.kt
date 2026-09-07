package com.taskmanager.presentation.screens.focus

import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.domain.logger.Logger
import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.usecase.pomodoro.GetPomodoroStatsUseCase
import com.taskmanager.domain.usecase.pomodoro.PomodoroStats
import com.taskmanager.domain.usecase.pomodoro.SavePomodoroSessionUseCase
import com.taskmanager.domain.usecase.settings.UserPreferences
import com.taskmanager.domain.usecase.task.GetAllTasksUseCase
import com.taskmanager.domain.usecase.task.GetTaskByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    val cyclePosition: Int = 0, // 0..pomodorosBeforeLongBreak-1
    val stats: PomodoroStats? = null,
    val tasks: List<Task> = emptyList(),
    val showSettings: Boolean = false,
    val showTaskPicker: Boolean = false,
    // Settings
    val workDuration: Int = 25,
    val shortBreakDuration: Int = 5,
    val longBreakDuration: Int = 15,
    val pomodorosBeforeLongBreak: Int = 4,
    val autoStartBreaks: Boolean = false,
    val autoStartPomodoros: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val dailyGoal: Int = 8,
)

@HiltViewModel
class FocusViewModel @Inject constructor(
    @ApplicationContext private val appContext: android.content.Context,
    private val savePomodoroSessionUseCase: SavePomodoroSessionUseCase,
    private val getPomodoroStatsUseCase: GetPomodoroStatsUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val userPreferences: UserPreferences,
    private val logger: Logger
) : ViewModel() {

    private val _state = MutableStateFlow(FocusUiState())
    val state: StateFlow<FocusUiState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadSettings()
        observeStats()
        observeTasks()
    }

    private fun loadSettings() {
        val work = userPreferences.pomodoroWorkDuration
        val short = userPreferences.pomodoroShortBreakDuration
        val long = userPreferences.pomodoroLongBreakDuration
        val beforeLong = userPreferences.pomodorosBeforeLongBreak
        _state.value = _state.value.copy(
            workDuration = work,
            shortBreakDuration = short,
            longBreakDuration = long,
            pomodorosBeforeLongBreak = beforeLong,
            autoStartBreaks = userPreferences.pomodoroAutoStartBreaks,
            autoStartPomodoros = userPreferences.pomodoroAutoStartPomodoros,
            soundEnabled = userPreferences.pomodoroSoundEnabled,
            vibrationEnabled = userPreferences.pomodoroVibrationEnabled,
            dailyGoal = userPreferences.pomodoroDailyGoal,
            durationMinutes = work,
            remainingSeconds = work * 60,
            totalSeconds = work * 60
        )
    }

    private fun observeStats() {
        viewModelScope.launch {
            try {
                getPomodoroStatsUseCase().collect { stats ->
                    _state.value = _state.value.copy(
                        stats = stats,
                        completedPomodoros = stats.todayCount
                    )
                }
            } catch (e: Exception) {
                logger.error("FocusViewModel", "Error in observeStats", e)
            }
        }
    }

    private fun observeTasks() {
        viewModelScope.launch {
            try {
                getAllTasksUseCase().collect { tasks ->
                    _state.value = _state.value.copy(tasks = tasks)
                }
            } catch (e: Exception) {
                logger.error("FocusViewModel", "Error observing tasks", e)
            }
        }
    }

    fun selectTask(taskId: Long?) {
        _state.value = _state.value.copy(taskId = taskId, taskTitle = null, showTaskPicker = false)
        if (taskId != null) {
            viewModelScope.launch {
                try {
                    val task = getTaskByIdUseCase(taskId)
                    _state.value = _state.value.copy(taskTitle = task?.title)
                } catch (e: Exception) {
                    logger.error("FocusViewModel", "Error loading task", e)
                }
            }
        }
    }

    fun start() {
        if (_state.value.isRunning) return
        _state.value = _state.value.copy(isRunning = true)
        timerJob = viewModelScope.launch {
            try {
                while (_state.value.remainingSeconds > 0) {
                    delay(1000)
                    _state.value = _state.value.copy(
                        remainingSeconds = _state.value.remainingSeconds - 1
                    )
                }
                onPhaseComplete()
            } catch (e: Exception) {
                logger.error("FocusViewModel", "Timer error", e)
            }
        }
    }

    fun pause() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
    }

    fun reset() {
        timerJob?.cancel()
        applyDurationForType(_state.value.type)
        _state.value = _state.value.copy(isRunning = false)
    }

    fun skip() {
        timerJob?.cancel()
        _state.value = _state.value.copy(isRunning = false)
        moveToNextPhase(autoStart = false)
    }

    fun selectType(type: PomodoroType) {
        timerJob?.cancel()
        applyDurationForType(type)
        _state.value = _state.value.copy(isRunning = false)
    }

    private fun applyDurationForType(type: PomodoroType) {
        val minutes = when (type) {
            PomodoroType.WORK -> _state.value.workDuration
            PomodoroType.SHORT_BREAK -> _state.value.shortBreakDuration
            PomodoroType.LONG_BREAK -> _state.value.longBreakDuration
        }
        _state.value = _state.value.copy(
            type = type,
            durationMinutes = minutes,
            remainingSeconds = minutes * 60,
            totalSeconds = minutes * 60
        )
    }

    private fun onPhaseComplete() {
        playCompletionNotification()
        viewModelScope.launch {
            try {
                if (_state.value.type == PomodoroType.WORK) {
                    savePomodoroSessionUseCase(
                        taskId = _state.value.taskId,
                        durationMinutes = _state.value.durationMinutes,
                        type = PomodoroType.WORK
                    )
                }
                moveToNextPhase(
                    autoStart = when (_state.value.type) {
                        PomodoroType.WORK -> _state.value.autoStartBreaks
                        else -> _state.value.autoStartPomodoros
                    }
                )
            } catch (e: Exception) {
                logger.error("FocusViewModel", "Error in onPhaseComplete", e)
            }
        }
    }

    private fun moveToNextPhase(autoStart: Boolean) {
        val currentType = _state.value.type
        val beforeLong = _state.value.pomodorosBeforeLongBreak
        val newCyclePos: Int
        val newType: PomodoroType

        when (currentType) {
            PomodoroType.WORK -> {
                newCyclePos = (_state.value.cyclePosition + 1) % beforeLong
                newType = if (newCyclePos == 0) PomodoroType.LONG_BREAK else PomodoroType.SHORT_BREAK
            }
            else -> {
                newCyclePos = if (currentType == PomodoroType.LONG_BREAK) 0 else _state.value.cyclePosition
                newType = PomodoroType.WORK
            }
        }

        applyDurationForType(newType)
        _state.value = _state.value.copy(
            cyclePosition = newCyclePos,
            isRunning = false
        )

        if (autoStart) {
            start()
        }
    }

    private fun playCompletionNotification() {
        if (_state.value.soundEnabled) {
            try {
                val mp = MediaPlayer.create(appContext, android.media.RingtoneManager.TYPE_NOTIFICATION)
                // Use system notification sound instead
                val soundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)
                if (soundUri != null) {
                    val ringtone = android.media.RingtoneManager.getRingtone(appContext, soundUri)
                    ringtone?.play()
                }
            } catch (e: Exception) {
                logger.error("FocusViewModel", "Sound error", e)
            }
        }
        if (_state.value.vibrationEnabled) {
            try {
                val vibrator = appContext.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(500)
                }
            } catch (e: Exception) {
                logger.error("FocusViewModel", "Vibration error", e)
            }
        }
    }

    // ─── Settings ───
    fun showSettingsDialog() { _state.value = _state.value.copy(showSettings = true) }
    fun hideSettingsDialog() { _state.value = _state.value.copy(showSettings = false) }
    fun showTaskPicker() { _state.value = _state.value.copy(showTaskPicker = true) }
    fun hideTaskPicker() { _state.value = _state.value.copy(showTaskPicker = false) }

    fun updateSettings(
        workDuration: Int,
        shortBreakDuration: Int,
        longBreakDuration: Int,
        pomodorosBeforeLongBreak: Int,
        autoStartBreaks: Boolean,
        autoStartPomodoros: Boolean,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean,
        dailyGoal: Int
    ) {
        userPreferences.pomodoroWorkDuration = workDuration
        userPreferences.pomodoroShortBreakDuration = shortBreakDuration
        userPreferences.pomodoroLongBreakDuration = longBreakDuration
        userPreferences.pomodorosBeforeLongBreak = pomodorosBeforeLongBreak
        userPreferences.pomodoroAutoStartBreaks = autoStartBreaks
        userPreferences.pomodoroAutoStartPomodoros = autoStartPomodoros
        userPreferences.pomodoroSoundEnabled = soundEnabled
        userPreferences.pomodoroVibrationEnabled = vibrationEnabled
        userPreferences.pomodoroDailyGoal = dailyGoal

        loadSettings()
        // Reapply duration if timer is not running
        if (!_state.value.isRunning) {
            applyDurationForType(_state.value.type)
        }
        hideSettingsDialog()
    }
}