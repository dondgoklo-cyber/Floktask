package com.taskmanager.domain.model

import java.time.Instant

/**
 * An audit-trail entry for a task change (issue 38: "User X changed priority
 * to HIGH at 14:30"). Immutable append-only event log.
 */
data class ActivityEvent(
    val id: Long? = null,
    val taskId: Long,
    val actor: String,
    val action: ActivityAction,
    val field: String? = null,
    val oldValue: String? = null,
    val newValue: String? = null,
    val timestamp: Instant = Instant.now()
) {
    fun describe(): String = buildString {
        append(actor)
        append(' ')
        append(action.verb)
        if (field != null) {
            append(' ')
            append(field)
            if (oldValue != null && newValue != null) {
                append(" from ")
                append(oldValue)
                append(" to ")
                append(newValue)
            } else if (newValue != null) {
                append(" to ")
                append(newValue)
            }
        }
        append(" at ")
        append(timestamp.toString())
    }
}

enum class ActivityAction(val verb: String) {
    CREATED("created"),
    UPDATED("updated"),
    COMPLETED("completed"),
    REOPENED("reopened"),
    DELETED("deleted"),
    COMMENTED("commented on")
}
