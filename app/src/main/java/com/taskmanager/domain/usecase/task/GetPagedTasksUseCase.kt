package com.taskmanager.domain.usecase.task

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.taskmanager.data.repository.toDomain
import com.taskmanager.domain.model.Task
import com.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Returns a paginated stream of tasks (issue 25: getAll() loads everything
 * at once → lag at 1000+ tasks). Uses Paging3 with a Room PagingSource.
 */
class GetPagedTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(pageSize: Int = 20): Flow<PagingData<Task>> =
        taskRepository.pagedTasks(pageSize)
}
