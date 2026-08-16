package com.taskmanager.domain.usecase.pomodoro

import com.taskmanager.domain.model.PomodoroSession
import com.taskmanager.domain.repository.PomodoroSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class PomodoroStats(
    val todayCount: Int,
    val todayMinutes: Int,
    val weekCount: Int,
    val weekMinutes: Int,
    val monthCount: Int,
    val monthMinutes: Int
)

class GetPomodoroStatsUseCase @Inject constructor(
    private val pomodoroSessionRepository: PomodoroSessionRepository
) {
    operator fun invoke(): Flow<PomodoroStats> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now()
        val dayStart = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val dayEnd = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val weekStart = today.minusDays(6).atStartOfDay(zone).toInstant().toEpochMilli()
        val monthStart = today.minusDays(29).atStartOfDay(zone).toInstant().toEpochMilli()

        return pomodoroSessionRepository.getAllSessions().map { sessions ->
            val workSessions = sessions.filter { it.isCompleted && it.type == com.taskmanager.domain.model.PomodoroType.WORK }
            val today = workSessions.filter { it.createdAt.toEpochMilli() in dayStart until dayEnd }
            val week = workSessions.filter { it.createdAt.toEpochMilli() in weekStart until dayEnd }
            val month = workSessions.filter { it.createdAt.toEpochMilli() in monthStart until dayEnd }

            PomodoroStats(
                todayCount = today.size,
                todayMinutes = today.sumOf { it.durationMinutes },
                weekCount = week.size,
                weekMinutes = week.sumOf { it.durationMinutes },
                monthCount = month.size,
                monthMinutes = month.sumOf { it.durationMinutes }
            )
        }
    }
}
