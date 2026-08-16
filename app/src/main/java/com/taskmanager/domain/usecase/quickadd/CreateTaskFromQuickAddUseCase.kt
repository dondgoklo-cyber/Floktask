package com.taskmanager.domain.usecase.quickadd

import com.taskmanager.domain.model.Project
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.nlp.ParsedTask
import com.taskmanager.domain.nlp.QuickAddParser
import com.taskmanager.domain.repository.ProjectRepository
import com.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * Parses a natural-language quick-add string and creates a [Task],
 * resolving (or creating) the referenced @project by name.
 *
 * Tags parsed from the input are returned via [Result.parsedTags] but are
 * not yet persisted until the many-to-many tag relation lands (issue 4).
 */
class CreateTaskFromQuickAddUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val parser: QuickAddParser = QuickAddParser()
) {

    suspend operator fun invoke(input: String): Result {
        if (input.isBlank()) return Result.Empty

        val parsed = parser.parse(input)
        if (parsed.title.isBlank()) return Result.Empty

        var projectId: Long? = null
        if (!parsed.projectName.isNullOrBlank()) {
            projectId = resolveProject(parsed.projectName)
        }

        val task = Task(
            title = parsed.title,
            deadline = parsed.deadline,
            priority = parsed.priority ?: com.taskmanager.domain.model.Priority.NONE,
            projectId = projectId
        )
        val id = taskRepository.createTask(task)

        return Result.Created(
            taskId = id,
            parsed = parsed,
            projectId = projectId
        )
    }

    private suspend fun resolveProject(name: String): Long {
        projectRepository.findProjectByName(name)?.let { return it.id ?: 0L }
        val created = Project(title = name)
        return projectRepository.createProject(created)
    }

    sealed class Result {
        data object Empty : Result()
        data class Created(
            val taskId: Long,
            val parsed: ParsedTask,
            val projectId: Long?
        ) : Result()
    }
}
