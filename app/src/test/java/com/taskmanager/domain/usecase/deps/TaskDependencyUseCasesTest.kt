package com.taskmanager.domain.usecase.deps

import com.taskmanager.domain.model.TaskDependency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskDependencyUseCasesTest {

    private val useCases = TaskDependencyUseCases()

    private fun dep(task: Long, blocks: Long) = TaskDependency(taskId = task, blocksTaskId = blocks)

    @Test
    fun `blocked tasks exclude those with completed dependencies`() {
        val deps = listOf(dep(2, 1), dep(3, 1), dep(4, 2))
        val blocked = useCases.blockedTaskIds(deps, completedTaskIds = setOf(1L))
        // 1 completed -> 2 and 3 unblocked; 4 still blocked by 2 if 2 incomplete
        assertEquals(setOf(4L), blocked)
    }

    @Test
    fun `all dependencies complete yields no blocked`() {
        val deps = listOf(dep(2, 1))
        assertTrue(useCases.blockedTaskIds(deps, completedTaskIds = setOf(1L)).isEmpty())
    }

    @Test
    fun `self-dependency is a cycle`() {
        assertTrue(useCases.wouldCreateCycle(dep(1, 1), emptyList()))
    }

    @Test
    fun `direct cycle detected`() {
        // existing 1->2; adding 2->1 should be a cycle
        val existing = listOf(dep(2, 1))
        assertTrue(useCases.wouldCreateCycle(dep(1, 2), existing))
    }

    @Test
    fun `transitive cycle detected`() {
        // 2 blocks 1, 3 blocks 2; adding 1 blocks 3 → cycle
        val existing = listOf(dep(1, 2), dep(2, 3))
        assertTrue(useCases.wouldCreateCycle(dep(3, 1), existing))
    }

    @Test
    fun `acyclic addition is allowed`() {
        // 2 blocks 1; adding 3 blocks 2 is fine
        val existing = listOf(dep(1, 2))
        assertFalse(useCases.wouldCreateCycle(dep(2, 3), existing))
    }

    @Test
    fun `prerequisites returned correctly`() {
        val deps = listOf(dep(1, 2), dep(1, 3), dep(4, 5))
        assertEquals(listOf(2L, 3L), useCases.prerequisites(1, deps))
    }
}
