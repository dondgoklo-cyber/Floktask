package com.taskmanager.domain.usecase.pomodoro

import com.taskmanager.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskFocusMinutesUseCase @Inject constructor(
    private val pomodoroRepository: PomodoroRepository
) {
    operator fun invoke(taskId: Long): Flow<Int> =
        pomodoroRepository.getFocusMinutesForTask(taskId)
}
