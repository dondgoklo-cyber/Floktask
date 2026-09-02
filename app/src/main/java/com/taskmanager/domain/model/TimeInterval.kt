package com.taskmanager.domain.model

import java.time.Instant

/**
 * A time-bounded interval with an optional origin id, used for conflict
 * detection across time blocks, task deadline windows, calendar events, etc.
 */
data class TimeInterval(
    val start: Instant,
    val end: Instant,
    val sourceId: Long? = null,
    val title: String? = null
) {
    init {
        require(!end.isBefore(start)) { "end must not be before start" }
    }

    /**
     * Half-open overlap: [start, end) - touching intervals (one ends exactly
     * when another starts are NOT considered conflicting.
     */
    fun overlaps(other: TimeInterval): Boolean =
        start < other.end && other.start < end
}
