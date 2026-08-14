package com.taskmanager.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PriorityTest {

    @Test
    fun priority_values_are_distinct_and_ordered() {
        assertEquals(1, Priority.HIGH.value)
        assertEquals(2, Priority.MEDIUM.value)
        assertEquals(3, Priority.LOW.value)
        assertEquals(4, Priority.NONE.value)
    }

    @Test
    fun priority_enum_has_four_entries() {
        assertEquals(4, Priority.entries.size)
    }

    @Test
    fun recurrenceRule_has_five_variants() {
        assertEquals(5, RecurrenceRule.entries.size)
    }

    @Test
    fun taskStatus_has_three_variants() {
        assertEquals(3, TaskStatus.entries.size)
    }

    @Test
    fun eisenhowerQuadrant_has_four_quadrants() {
        assertEquals(4, EisenhowerQuadrant.entries.size)
    }
}
