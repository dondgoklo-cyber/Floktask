package com.taskmanager.domain.usecase.pomodoro

import com.taskmanager.domain.model.PomodoroType
import com.taskmanager.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetPomodoroStatsUseCase @Inject constructor(
    private val pomodoroRepository: PomodoroRepository
) {

    operator fun invoke(): Flow<PomodoroStats> =
        pomodoroRepository.getCompletedSessions().map { sessions ->
            val zone = ZoneId.systemDefault()
            val todayStart = LocalDate.now(zone)
                .atStartOfDay(zone)
                .toInstant()

            val workSessions = sessions.filter { it.type == PomodoroType.WORK }

            val completedToday = workSessions.count {
                !it.startTime.isBefore(todayStart)
            }

            val totalFocusMinutes = workSessions.sumOf { it.durationMinutes }

            PomodoroStats(
                completedWorkSessions = workSessions.size,
                totalFocusMinutes = totalFocusMinutes,
                completedToday = completedToday
            )
        }
}
