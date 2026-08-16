package com.taskmanager.domain.usecase.activity

import com.taskmanager.domain.model.ActivityAction
import com.taskmanager.domain.model.ActivityEvent
import com.taskmanager.domain.model.Task
import java.time.Instant
import javax.inject.Inject

/**
 * Builds audit-trail events from task transitions (issue 38).
 * Produces a list because a single update may touch several fields.
 */
class RecordTaskChangeUseCase @Inject constructor() {

    /**
     * @param before the task before the change (null for creation)
     * @param after the task after the change (null for deletion)
     * @param actor who made the change
     */
    operator fun invoke(
        before: Task?,
        after: Task?,
        actor: String = "user",
        now: Instant = Instant.now()
    ): List<ActivityEvent> {
        if (before == null && after == null) return emptyList()

        // Creation.
        if (before == null && after != null) {
            return listOf(
                ActivityEvent(
                    taskId = after.id ?: 0,
                    actor = actor,
                    action = ActivityAction.CREATED,
                    timestamp = now
                )
            )
        }
        // Deletion.
        if (before != null && after == null) {
            return listOf(
                ActivityEvent(
                    taskId = before.id ?: 0,
                    actor = actor,
                    action = ActivityAction.DELETED,
                    timestamp = now
                )
            )
        }
        // Update — diff fields.
        val b = before!!
        val a = after!!
        val events = mutableListOf<ActivityEvent>()
        val base = ActivityEvent(
            taskId = a.id ?: 0,
            actor = actor,
            action = ActivityAction.UPDATED,
            timestamp = now
        )

        if (b.title != a.title) events.add(base.copy(field = "title", oldValue = b.title, newValue = a.title))
        if (b.priority != a.priority) events.add(
            base.copy(field = "priority", oldValue = b.priority.name, newValue = a.priority.name)
        )
        if (b.isCompleted != a.isCompleted) events.add(
            base.copy(
                action = if (a.isCompleted) ActivityAction.COMPLETED else ActivityAction.REOPENED,
                field = null
            )
        )
        if (b.projectId != a.projectId) events.add(
            base.copy(field = "projectId", oldValue = b.projectId?.toString(), newValue = a.projectId?.toString())
        )
        if (b.deadline != a.deadline) events.add(
            base.copy(field = "deadline", oldValue = b.deadline?.toString(), newValue = a.deadline?.toString())
        )

        return events
    }

}
