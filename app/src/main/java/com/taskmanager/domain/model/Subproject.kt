package com.taskmanager.domain.model

import java.time.Instant

/**
 * Subproject model for hierarchical project structure (2-3 levels deep)
 * Each subproject can have its own tasks and nested subprojects
 */
data class Subproject(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val parentProjectId: Long? = null,  // Can be a main project or another subproject
    val parentSubprojectId: Long? = null,  // For 3rd level nesting
    val color: String? = null,
    val icon: String? = null,
    val deadline: Instant? = null,
    val isArchived: Boolean = false,
    val orderIndex: Int = 0,  // For sorting within parent
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) {
    /**
     * Check if this is a top-level subproject (direct child of a main project)
     */
    val isTopLevel: Boolean
        get() = parentSubprojectId == null && parentProjectId != null

    /**
     * Check if this is a nested subproject (child of another subproject)
     */
    val isNested: Boolean
        get() = parentSubprojectId != null

    /**
     * Get the nesting level (1 for top-level, 2 for nested under subproject)
     */
    val nestingLevel: Int
        get() = if (parentSubprojectId == null) 1 else 2
}
