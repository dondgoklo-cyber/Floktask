package com.taskmanager.domain.usecase.habit

import java.time.DayOfWeek
import com.taskmanager.domain.model.Habit
import com.taskmanager.domain.model.HabitCompletion
import com.taskmanager.domain.model.HabitFrequency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GetHabitStreakUseCaseTest {

    private val useCase = GetHabitStreakUseCase()
    private val today = LocalDate.now()

    private fun completion(habitId: Long, date: LocalDate) = HabitCompletion(habitId = habitId, date = date)
    private fun habit(id: Long = 1, freq: HabitFrequency = HabitFrequency.DAILY) =
        Habit(id = id, title = "h", frequency = freq)

    @Test
    fun `empty completions yield zero streak`() {
        val streak = useCase(habit(), emptyList())
        assertEquals(0, streak.currentStreak)
        assertEquals(0, streak.longestStreak)
    }

    @Test
    fun `current streak counts consecutive days ending today`() {
        val completions = listOf(
            completion(1, today),
            completion(1, today.minusDays(1)),
            completion(1, today.minusDays(2))
        )
        assertEquals(3, useCase(habit(), completions).currentStreak)
    }

    @Test
    fun `current streak tolerates today not done yet`() {
        val completions = listOf(
            completion(1, today.minusDays(1)),
            completion(1, today.minusDays(2))
        )
        assertEquals(2, useCase(habit(), completions).currentStreak)
    }

    @Test
    fun `streak breaks on gap`() {
        val completions = listOf(
            completion(1, today),
            completion(1, today.minusDays(2))
        )
        assertEquals(1, useCase(habit(), completions).currentStreak)
        assertEquals(1, useCase(habit(), completions).longestStreak)
    }

    @Test
    fun `longest streak across history`() {
        val start = today.minusDays(50)
        val completions = (0 until 5).map { completion(1, start.plusDays(it.toLong())) } +
            listOf(completion(1, today))
        assertEquals(5, useCase(habit(), completions).longestStreak)
    }

    @Test
    fun `daily habit due every day`() {
        assertTrue(useCase.isDueToday(habit(freq = HabitFrequency.DAILY), today))
    }

    @Test
    fun `weekday habit not due on weekend`() {
        val saturday = today.with(DayOfWeek.SATURDAY)
        val h = habit(freq = HabitFrequency.WEEKDAYS)
        // If today is already Saturday this is straightforward; otherwise test the Saturday directly.
        assertFalse(useCase.isDueToday(h, saturday))
    }

    @Test
    fun `custom habit due only on target days`() {
        val h = habit(freq = HabitFrequency.CUSTOM).copy(targetDaysOfWeek = setOf(DayOfWeek.MONDAY))
        val monday = today.with(DayOfWeek.MONDAY)
        assertTrue(useCase.isDueToday(h, monday))
        val tuesday = today.with(DayOfWeek.TUESDAY)
        assertFalse(useCase.isDueToday(h, tuesday))
    }
}
