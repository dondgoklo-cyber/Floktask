package com.taskmanager.domain.usecase.energy

import com.taskmanager.domain.model.DayPart
import com.taskmanager.domain.model.EnergyLevel
import com.taskmanager.domain.model.EnergyLog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetEnergyProfileUseCaseTest {

    private val useCase = GetEnergyProfileUseCase()

    private fun log(part: DayPart, level: EnergyLevel) =
        EnergyLog(dayPart = part, level = level)

    @Test
    fun `empty logs yield no best part`() {
        val profile = useCase(emptyList())
        assertNull(profile.bestDayPart)
    }

    @Test
    fun `best part is the highest average`() {
        val logs = listOf(
            log(DayPart.MORNING, EnergyLevel.HIGH),
            log(DayPart.MORNING, EnergyLevel.HIGH),
            log(DayPart.AFTERNOON, EnergyLevel.LOW),
            log(DayPart.EVENING, EnergyLevel.MEDIUM)
        )
        val profile = useCase(logs)
        assertEquals(DayPart.MORNING, profile.bestDayPart)
    }

    @Test
    fun `averages computed per part`() {
        val logs = listOf(
            log(DayPart.MORNING, EnergyLevel.HIGH),   // 3
            log(DayPart.MORNING, EnergyLevel.LOW),    // 1 → avg 2.0
            log(DayPart.AFTERNOON, EnergyLevel.MEDIUM) // 2 → avg 2.0
        )
        val profile = useCase(logs)
        assertEquals(2.0, profile.averageByDayPart[DayPart.MORNING]!!, 0.001)
        assertEquals(2.0, profile.averageByDayPart[DayPart.AFTERNOON]!!, 0.001)
        assertEquals(0.0, profile.averageByDayPart[DayPart.EVENING]!!, 0.001)
    }

    @Test
    fun `suggestDayPart falls back to morning without data`() {
        assertEquals(DayPart.MORNING, useCase.suggestDayPart(useCase(emptyList())))
    }

    @Test
    fun `suggestDayPart returns best when data exists`() {
        val logs = listOf(
            log(DayPart.EVENING, EnergyLevel.HIGH),
            log(DayPart.MORNING, EnergyLevel.LOW)
        )
        val profile = useCase(logs)
        assertEquals(DayPart.EVENING, useCase.suggestDayPart(profile))
    }
}
