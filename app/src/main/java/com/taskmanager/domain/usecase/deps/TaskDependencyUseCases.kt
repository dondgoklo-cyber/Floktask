package com.taskmanager.domain.usecase.deps

import com.taskmanager.domain.model.TaskDependency
import javax.inject.Inject

/**
 * Operations over task dependencies (issue 40: "Task B blocked by Task A",
 * visual indicators, auto-reschedule on delay).
 *
 * Graph model: an edge blocksTaskId -> taskId means taskId is blocked by
 * blocksTaskId (i.e. taskId depends on blocksTaskId).
 */
class TaskDependencyUseCases @Inject constructor() {

    /**
     * Returns the set of task ids that are currently blocked (have at least
     * one unfinished dependency).
     *
     * @param dependencies all dependency edges
     * @param completedTaskIds ids of tasks already completed (these no longer block)
     */
    fun blockedTaskIds(
        dependencies: List<TaskDependency>,
        completedTaskIds: Set<Long>
    ): Set<Long> {
        return dependencies
            .filter { it.blocksTaskId !in completedTaskIds }
            .map { it.taskId }
            .toSet()
    }

    /**
     * Detects whether adding [candidate] would create a cycle given the
     * existing [dependencies]. A task cannot depend on itself or form a loop.
     */
    fun wouldCreateCycle(
        candidate: TaskDependency,
        dependencies: List<TaskDependency>
    ): Boolean {
        if (candidate.taskId == candidate.blocksTaskId) return true
        // Build edges: taskId depends on blocksTaskId (edge blocksTaskId -> taskId).
        // A new edge blocksTaskId -> taskId creates a cycle if there is already
        // a path taskId -> ... -> blocksTaskId.
        val adj = dependencies.groupBy({ it.blocksTaskId }, { it.taskId })
            .mapValues { it.value.toMutableList() }
            .toMutableMap()
        // Temporarily add the candidate edge.
        adj.getOrPut(candidate.blocksTaskId) { mutableListOf() }.add(candidate.taskId)
        // DFS from candidate.blocksTaskId; if we reach candidate.taskId via the
        // existing graph (excluding the direct new edge), it's a cycle.
        return hasPath(adj, from = candidate.taskId, target = candidate.blocksTaskId)
    }

    /**
     * Tasks that directly block [taskId] (its prerequisite tasks).
     */
    fun prerequisites(taskId: Long, dependencies: List<TaskDependency>): List<Long> =
        dependencies.filter { it.taskId == taskId }.map { it.blocksTaskId }

    private fun hasPath(
        adj: Map<Long, List<Long>>,
        from: Long,
        target: Long,
        visited: MutableSet<Long> = mutableSetOf()
    ): Boolean {
        if (from == target) return true
        if (from in visited) return false
        visited.add(from)
        adj[from]?.forEach { neighbor ->
            if (hasPath(adj, neighbor, target, visited)) return true
        }
        return false
    }
}
