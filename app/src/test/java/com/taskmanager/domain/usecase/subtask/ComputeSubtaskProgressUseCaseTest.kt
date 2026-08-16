package com.taskmanager.domain.usecase.subtask

import com.taskmanager.domain.model.Subtask
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ComputeSubtaskProgressUseCaseTest {

    private val useCase = ComputeSubtaskProgressUseCase()

    private fun sub(completed: Boolean, parent: Long = 1) =
        Subtask(parentTaskId = parent, title = "s", isCompleted = completed)

    @Test
    fun `empty list yields zero progress`() {
        val p = useCase(emptyList())
        assertEquals(0, p.total)
        assertEquals(0, p.completed)
        assertEquals(0f, p.ratio, 0.001f)
    }

    @Test
    fun `half completed yields half ratio`() {
        val p = useCase(listOf(sub(true), sub(false)))
        assertEquals(2, p.total)
        assertEquals(1, p.completed)
        assertEquals(0.5f, p.ratio, 0.001f)
    }

    @Test
    fun `all completed yields full progress`() {
        val p = useCase(listOf(sub(true), sub(true), sub(true)))
        assertEquals(3, p.completed)
        assertEquals(1f, p.ratio, 0.001f)
        assertTrue(p.isFullyCompleted)
    }

    @Test
    fun `shouldCompleteParent true only when all done`() {
        assertFalse(useCase.shouldCompleteParent(listOf(sub(true), sub(false))))
        assertTrue(useCase.shouldCompleteParent(listOf(sub(true), sub(true))))
    }

    @Test
    fun `shouldCompleteParent false for empty list`() {
        assertFalse(useCase.shouldCompleteParent(emptyList()))
    }

    @Test
    fun `parentTaskId from first subtask`() {
        val p = useCase(listOf(sub(true, parent = 42), sub(false, parent = 42)))
        assertEquals(42L, p.parentTaskId)
    }
}
