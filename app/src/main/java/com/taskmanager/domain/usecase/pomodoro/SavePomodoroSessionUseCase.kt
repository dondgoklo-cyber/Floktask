package com.taskmanager.domain.usecase.pomodoro

import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.domain.repository.PomodoroRepository
import javax.inject.Inject

class SavePomodoroSessionUseCase @Inject constructor(
    private val pomodoroRepository: PomodoroRepository
) {
    suspend operator fun invoke(
        taskId: Long?,
        durationMinutes: Int,
        type: PomodoroType
    ): Long = pomodoroRepository.saveSession(
        taskId = taskId,
        durationMinutes = durationMinutes,
        type = type
    )
}
