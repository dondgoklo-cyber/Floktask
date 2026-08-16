package com.taskmanager.domain.usecase.search

import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.SearchResult
import com.taskmanager.domain.model.SearchResultType
import com.taskmanager.domain.model.Tag
import com.taskmanager.domain.model.Task
import javax.inject.Inject

/**
 * Unified search across tasks, projects, and tags (issue 27: search worked
 * only for tasks). Pure function over in-memory lists — the repository layer
 * supplies the data; matching is case-insensitive containment.
 */
class GlobalSearchUseCase @Inject constructor() {

    operator fun invoke(
        query: String,
        tasks: List<Task>,
        projects: List<Project>,
        tags: List<Tag>
    ): List<SearchResult> {
        val q = query.trim()
        if (q.isEmpty()) return emptyList()
        val results = mutableListOf<SearchResult>()

        tasks.filter { it.title.contains(q, ignoreCase = true) }.forEach { task ->
            results.add(
                SearchResult(
                    id = task.id ?: 0,
                    title = task.title,
                    type = SearchResultType.TASK,
                    subtitle = task.description
                )
            )
        }
        projects.filter { it.title.contains(q, ignoreCase = true) }.forEach { project ->
            results.add(
                SearchResult(
                    id = project.id ?: 0,
                    title = project.title,
                    type = SearchResultType.PROJECT,
                    subtitle = project.description
                )
            )
        }
        tags.filter { it.name.contains(q, ignoreCase = true) }.forEach { tag ->
            results.add(
                SearchResult(
                    id = tag.id ?: 0,
                    title = tag.name,
                    type = SearchResultType.TAG
                )
            )
        }
        return results
    }
}
