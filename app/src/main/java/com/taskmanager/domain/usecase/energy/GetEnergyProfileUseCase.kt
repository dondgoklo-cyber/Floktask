package com.taskmanager.domain.usecase.energy

import com.taskmanager.domain.model.DayPart
import com.taskmanager.domain.model.EnergyLevel
import com.taskmanager.domain.model.EnergyLog
import javax.inject.Inject

/**
 * Aggregated energy profile per part of the day.
 */
data class EnergyProfile(
    val averageByDayPart: Map<DayPart, Double>,
    val bestDayPart: DayPart?
)

/**
 * Learns the user's most productive time of day from recorded energy logs
 * (issue 17) and suggests when to tackle a high-priority task.
 */
class GetEnergyProfileUseCase @Inject constructor() {

    operator fun invoke(logs: List<EnergyLog>): EnergyProfile {
        if (logs.isEmpty()) return EnergyProfile(emptyMap(), null)

        val averages = DayPart.entries.associateWith { part ->
            val scores = logs.filter { it.dayPart == part }.map { it.level.score }
            if (scores.isEmpty()) 0.0 else scores.average()
        }

        val best = averages
            .filter { it.value > 0.0 }
            .maxByOrNull { it.value }
            ?.key

        return EnergyProfile(averages, best)
    }

    /**
     * Suggests the best part of day for a HIGH-priority task, falling back to
     * MORNING if no data exists.
     */
    fun suggestDayPart(profile: EnergyProfile): DayPart =
        profile.bestDayPart ?: DayPart.MORNING

}
