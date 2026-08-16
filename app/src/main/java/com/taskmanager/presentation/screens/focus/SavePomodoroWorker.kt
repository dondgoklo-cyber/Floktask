package com.taskmanager.presentation.screens.focus

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.taskmanager.domain.model.PomodoroSession
import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.domain.usecase.pomodoro.SavePomodoroSessionUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant

@HiltWorker
class SavePomodoroWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val savePomodoroSessionUseCase: SavePomodoroSessionUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val taskId = inputData.getLong("taskId", -1L).takeIf { it != -1L }
            val duration = inputData.getInt("duration", 25)
            val typeString = inputData.getString("type") ?: "WORK"

            val type = when (typeString) {
                "WORK" -> PomodoroType.WORK
                "SHORT_BREAK" -> PomodoroType.SHORT_BREAK
                "LONG_BREAK" -> PomodoroType.LONG_BREAK
                else -> PomodoroType.WORK
            }

            val session = PomodoroSession(
                taskId = taskId,
                startTime = Instant.now().minusSeconds(duration * 60L),
                durationMinutes = duration,
                isCompleted = true,
                type = type
            )

            savePomodoroSessionUseCase(
                taskId = session.taskId,
                durationMinutes = session.durationMinutes,
                type = session.type
            )

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
