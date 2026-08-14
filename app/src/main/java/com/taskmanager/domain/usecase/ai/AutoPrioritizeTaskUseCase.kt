package com.taskmanager.domain.usecase.ai

import com.taskmanager.domain.ai.TaskPrioritizer
import com.taskmanager.domain.model.Priority
import com.taskmanager.domain.model.Task
import javax.inject.Inject

class AutoPrioritizeTaskUseCase @Inject constructor() {
    operator fun invoke(task: Task): Priority = TaskPrioritizer.prioritize(task)
}
