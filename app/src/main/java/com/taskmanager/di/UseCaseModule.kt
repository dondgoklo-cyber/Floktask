package com.taskmanager.di

import com.taskmanager.domain.usecase.task.*
import com.taskmanager.domain.usecase.project.*
import com.taskmanager.domain.usecase.tag.*
import com.taskmanager.domain.usecase.habit.*
import com.taskmanager.domain.usecase.finance.*
import com.taskmanager.domain.usecase.eisenhower.*
import com.taskmanager.domain.usecase.kanban.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module providing all use cases for the application.
 * Centralized place for all business logic dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // ==================== TASK USE CASES ====================

    @Provides
    @Singleton
    fun provideGetTaskByIdUseCase(
        taskRepository: com.taskmanager.domain.repository.TaskRepository
    ): GetTaskByIdUseCase = GetTaskByIdUseCase(taskRepository)

    @Provides
    @Singleton
    fun provideGetAllTasksUseCase(
        taskRepository: com.taskmanager.domain.repository.TaskRepository
    ): GetAllTasksUseCase = GetAllTasksUseCase(taskRepository)

    @Provides
    @Singleton
    fun provideCreateTaskUseCase(
        taskRepository: com.taskmanager.domain.repository.TaskRepository
    ): CreateTaskUseCase = CreateTaskUseCase(taskRepository)

    @Provides
    @Singleton
    fun provideUpdateTaskUseCase(
        taskRepository: com.taskmanager.domain.repository.TaskRepository
    ): UpdateTaskUseCase = UpdateTaskUseCase(taskRepository)

    @Provides
    @Singleton
    fun provideSetTaskCompletedUseCase(
        taskRepository: com.taskmanager.domain.repository.TaskRepository
    ): SetTaskCompletedUseCase = SetTaskCompletedUseCase(taskRepository)

    @Provides
    @Singleton
    fun provideToggleTaskCompletionUseCase(
        taskRepository: com.taskmanager.domain.repository.TaskRepository
    ): ToggleTaskCompletionUseCase = ToggleTaskCompletionUseCase(taskRepository)

    @Provides
    @Singleton
    fun provideDeleteTaskUseCase(
        taskRepository: com.taskmanager.domain.repository.TaskRepository
    ): DeleteTaskUseCase = DeleteTaskUseCase(taskRepository)

    // ==================== PROJECT USE CASES ====================

    @Provides
    @Singleton
    fun provideGetAllProjectsUseCase(
        projectRepository: com.taskmanager.domain.repository.ProjectRepository
    ): GetAllProjectsUseCase = GetAllProjectsUseCase(projectRepository)

    @Provides
    @Singleton
    fun provideGetProjectByIdUseCase(
        projectRepository: com.taskmanager.domain.repository.ProjectRepository
    ): GetProjectByIdUseCase = GetProjectByIdUseCase(projectRepository)

    @Provides
    @Singleton
    fun provideCreateProjectUseCase(
        projectRepository: com.taskmanager.domain.repository.ProjectRepository
    ): CreateProjectUseCase = CreateProjectUseCase(projectRepository)

    @Provides
    @Singleton
    fun provideUpdateProjectUseCase(
        projectRepository: com.taskmanager.domain.repository.ProjectRepository
    ): UpdateProjectUseCase = UpdateProjectUseCase(projectRepository)

    @Provides
    @Singleton
    fun provideDeleteProjectUseCase(
        projectRepository: com.taskmanager.domain.repository.ProjectRepository
    ): DeleteProjectUseCase = DeleteProjectUseCase(projectRepository)

    // ==================== TAG USE CASES ====================

    @Provides
    @Singleton
    fun provideGetAllTagsUseCase(
        tagRepository: com.taskmanager.domain.repository.TagRepository
    ): GetAllTagsUseCase = GetAllTagsUseCase(tagRepository)

    @Provides
    @Singleton
    fun provideCreateTagUseCase(
        tagRepository: com.taskmanager.domain.repository.TagRepository
    ): CreateTagUseCase = CreateTagUseCase(tagRepository)

    // ==================== HABIT USE CASES ====================

    @Provides
    @Singleton
    fun provideGetActiveHabitsUseCase(
        habitRepository: com.taskmanager.domain.repository.HabitRepository
    ): GetActiveHabitsUseCase = GetActiveHabitsUseCase(habitRepository)

    @Provides
    @Singleton
    fun provideToggleHabitCompletionUseCase(
        habitLogRepository: com.taskmanager.domain.repository.HabitLogRepository
    ): ToggleHabitCompletionUseCase = ToggleHabitCompletionUseCase(habitLogRepository)

    @Provides
    @Singleton
    fun provideGetHabitStatsUseCase(
        habitLogRepository: com.taskmanager.domain.repository.HabitLogRepository
    ): GetHabitStatsUseCase = GetHabitStatsUseCase(habitLogRepository)

    // ==================== FINANCE USE CASES ====================

    @Provides
    @Singleton
    fun provideGetFinanceSummaryUseCase(
        transactionRepository: com.taskmanager.domain.repository.TransactionRepository
    ): GetFinanceSummaryUseCase = GetFinanceSummaryUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetRecentTransactionsUseCase(
        transactionRepository: com.taskmanager.domain.repository.TransactionRepository
    ): GetRecentTransactionsUseCase = GetRecentTransactionsUseCase(transactionRepository)

    // ==================== EISENHOWER USE CASES ====================

    @Provides
    @Singleton
    fun provideGetEisenhowerTasksUseCase(
        taskRepository: com.taskmanager.domain.repository.TaskRepository
    ): GetEisenhowerTasksUseCase = GetEisenhowerTasksUseCase(taskRepository)

    @Provides
    @Singleton
    fun provideUpdateEisenhowerQuadrantUseCase(
        taskRepository: com.taskmanager.domain.repository.TaskRepository
    ): UpdateEisenhowerQuadrantUseCase = UpdateEisenhowerQuadrantUseCase(taskRepository)

    // ==================== KANBAN USE CASES ====================

    @Provides
    @Singleton
    fun provideMoveTaskToStatusUseCase(
        taskRepository: com.taskmanager.domain.repository.TaskRepository
    ): MoveTaskToStatusUseCase = MoveTaskToStatusUseCase(taskRepository)
}
