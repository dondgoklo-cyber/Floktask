package com.taskmanager.domain.model

/**
 * A user's energy level recorded for a part of the day.
 * Used to learn when the user is most productive (issue 17).
 */
data class EnergyLog(
    val id: Long? = null,
    val dayPart: DayPart,
    val level: EnergyLevel,
    val recordedAt: java.time.LocalDate = java.time.LocalDate.now()
)

enum class DayPart { MORNING, AFTERNOON, EVENING }

enum class EnergyLevel(val score: Int) { LOW(1), MEDIUM(2), HIGH(3) }
