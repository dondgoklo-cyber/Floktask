package com.taskmanager.domain.model

/**
 * A unified search result across entity types (issue 27: search was
 * task-only). Carries the matched entity id + a display title.
 */
data class SearchResult(
    val id: Long,
    val title: String,
    val type: SearchResultType,
    val subtitle: String? = null
)

enum class SearchResultType { TASK, PROJECT, TAG }
