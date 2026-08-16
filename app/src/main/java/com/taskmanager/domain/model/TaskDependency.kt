package com.taskmanager.domain.model

/**
 * A "blocked-by" dependency: [taskId] depends on (is blocked by) [blocksTaskId].
 * Completing [blocksTaskId] unblocks [taskId] (issue 40).
 */
data class TaskDependency(
    val id: Long? = null,
    val taskId: Long,
    val blocksTaskId: Long
)
