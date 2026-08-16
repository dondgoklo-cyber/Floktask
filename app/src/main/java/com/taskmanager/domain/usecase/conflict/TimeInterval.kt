package com.taskmanager.domain.usecase.conflict

import java.time.Instant

/**
 * A time-bounded interval used by the auto-scheduler to represent busy/free slots.
 * (Mirrors the conflict-detection model; consolidated on merge.)
 */
data class TimeInterval(
    val start: Instant,
    val end: Instant
) {
    init {
        require(!end.isBefore(start)) { "end must not be before start" }
    }
}
