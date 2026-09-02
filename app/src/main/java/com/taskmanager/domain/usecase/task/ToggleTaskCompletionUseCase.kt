package com.taskmanager.domain.usecase.task

import javax.inject.Inject

/**
 * Use case for toggling task completion status.
 * Delegates to [SetTaskCompletedUseCase] so that completing a task cancels its reminder.
 */
class ToggleTaskCompletionUseCase @Inject constructor(
    private val setTaskCompletedUseCase: SetTaskCompletedUseCase
) {

    /**
     * Toggle task completion status
     * @param taskId The ID of the task to toggle
     * @return The new completion status
     */
    suspend operator fun invoke(taskId: Long): Boolean {
        return setTaskCompletedUseCase.toggle(taskId)
    }
}
