package com.taskmanager.domain.usecase.conflict

import com.taskmanager.domain.model.TimeInterval
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class DetectConflictsUseCaseTest {

    private val useCase = DetectConflictsUseCase()

    private fun interval(start: String, end: String, id: Long? = null) =
        TimeInterval(
            start = Instant.parse(start),
            end = Instant.parse(end),
            sourceId = id
        )

    @Test
    fun `overlapping intervals conflict`() {
        val existing = interval("2026-01-15T10:00:00Z", "2026-01-15T11:00:00Z", 1)
        val candidate = interval("2026-01-15T10:30:00Z", "2026-01-15T11:30:00Z")
        val result = useCase(candidate, listOf(existing))
        assertTrue(result.hasConflict)
        assertEquals(1, result.conflicting.size)
    }

    @Test
    fun `touching intervals do not conflict`() {
        val existing = interval("2026-01-15T10:00:00Z", "2026-01-15T11:00:00Z", 1)
        val candidate = interval("2026-01-15T11:00:00Z", "2026-01-15T12:00:00Z")
        val result = useCase(candidate, listOf(existing))
        assertFalse(result.hasConflict)
    }

    @Test
    fun `fully contained interval conflicts`() {
        val existing = interval("2026-01-15T09:00:00Z", "2026-01-15T17:00:00Z", 1)
        val candidate = interval("2026-01-15T10:00:00Z", "2026-01-15T11:00:00Z")
        assertTrue(useCase(candidate, listOf(existing)).hasConflict)
    }

    @Test
    fun `disjoint intervals do not conflict`() {
        val existing = interval("2026-01-15T10:00:00Z", "2026-01-15T11:00:00Z", 1)
        val candidate = interval("2026-01-15T12:00:00Z", "2026-01-15T13:00:00Z")
        assertFalse(useCase(candidate, listOf(existing)).hasConflict)
    }

    @Test
    fun `multiple conflicts returned`() {
        val a = interval("2026-01-15T10:00:00Z", "2026-01-15T11:00:00Z", 1)
        val b = interval("2026-01-15T10:30:00Z", "2026-01-15T11:30:00Z", 2)
        val c = interval("2026-01-15T11:00:00Z", "2026-01-15T12:00:00Z", 3) // touches c, no conflict with candidate
        val candidate = interval("2026-01-15T10:45:00Z", "2026-01-15T11:15:00Z")
        val result = useCase(candidate, listOf(a, b, c))
        assertEquals(2, result.conflicting.size)
    }

    @Test
    fun `findInternalConflicts detects pairwise overlaps`() {
        val a = interval("2026-01-15T10:00:00Z", "2026-01-15T11:00:00Z", 1)
        val b = interval("2026-01-15T10:30:00Z", "2026-01-15T11:30:00Z", 2)
        val c = interval("2026-01-15T12:00:00Z", "2026-01-15T13:00:00Z", 3)
        val conflicts = useCase.findInternalConflicts(listOf(a, b, c))
        assertEquals(1, conflicts.size)
        assertEquals(a to b, conflicts.first())
    }

    @Test
    fun `empty existing yields no conflict`() {
        val candidate = interval("2026-01-15T10:00:00Z", "2026-01-15T11:00:00Z")
        assertFalse(useCase(candidate, emptyList()).hasConflict)
    }
}
