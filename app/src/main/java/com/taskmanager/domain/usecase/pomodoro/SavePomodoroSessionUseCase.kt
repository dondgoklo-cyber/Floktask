package com.taskmanager.domain.usecase.pomodoro

import android.util.Log
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
    ): Long = runCatching {
        val session = PomodoroSession(
            taskId = taskId,
            startTime = Instant.now(),
            durationMinutes = durationMinutes,
            isCompleted = true,
            type = type
        )
        pomodoroSessionRepository.saveSession(session)
    }.onFailure { e ->
        Log.e("SavePomodoroSessionUseCase", "Error in invoke", e)
    }.getOrThrow()
}
