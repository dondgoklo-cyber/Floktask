package com.taskmanager.domain.usecase.conflict

import com.taskmanager.domain.model.TimeInterval
import javax.inject.Inject

/**
 * Result of a conflict check for a candidate interval against existing ones.
 */
data class ConflictResult(
    val hasConflict: Boolean,
    val conflicting: List<TimeInterval>
)

/**
 * Detects overlapping time intervals. Used by time-blocking to warn before
 * scheduling a block on an occupied slot (issue 12).
 *
 * Overlap is half-open: two intervals that touch at a boundary do not conflict.
 */
class DetectConflictsUseCase @Inject constructor() {

    /**
     * Returns all intervals in [existing] that overlap [candidate].
     */
    operator fun invoke(candidate: TimeInterval, existing: List<TimeInterval>): ConflictResult {
        val conflicting = existing.filter { it.overlaps(candidate) }
        return ConflictResult(
            hasConflict = conflicting.isNotEmpty(),
            conflicting = conflicting
        )
    }

    /**
     * Finds all pairwise conflicts within [intervals] (e.g. a whole day's blocks).
     */
    fun findInternalConflicts(intervals: List<TimeInterval>): List<Pair<TimeInterval, TimeInterval>> {
        val conflicts = mutableListOf<Pair<TimeInterval, TimeInterval>>()
        for (i in intervals.indices) {
            for (j in (i + 1) until intervals.size) {
                if (intervals[i].overlaps(intervals[j])) {
                    conflicts.add(intervals[i] to intervals[j])
                }
            }
        }
        return conflicts
    }
}
