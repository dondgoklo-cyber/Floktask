package com.taskmanager.domain.usecase.pomodoro

import com.taskmanager.domain.model.PomodoroSession
import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.domain.repository.PomodoroSessionRepository
import java.time.Instant
import javax.inject.Inject

class SavePomodoroSessionUseCase @Inject constructor(
    private val pomodoroSessionRepository: PomodoroSessionRepository
) {
    suspend operator fun invoke(
        taskId: Long?,
        durationMinutes: Int,
        type: PomodoroType = PomodoroType.WORK
    ): Long {
        val session = PomodoroSession(
            taskId = taskId,
            startTime = Instant.now(),
            durationMinutes = durationMinutes,
            isCompleted = true,
            type = type
        )
        return pomodoroSessionRepository.saveSession(session)
    }
}
