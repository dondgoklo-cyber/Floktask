package com.taskmanager.di

import com.taskmanager.data.repository.HabitLogRepositoryImpl
import com.taskmanager.data.repository.HabitRepositoryImpl
import com.taskmanager.data.repository.PomodoroSessionRepositoryImpl
import com.taskmanager.data.repository.ProjectRepositoryImpl
import com.taskmanager.data.repository.SubtaskRepositoryImpl
import com.taskmanager.data.repository.TagRepositoryImpl
import com.taskmanager.data.repository.TaskRepositoryImpl
import com.taskmanager.data.repository.UserStatsRepositoryImpl
import com.taskmanager.domain.repository.HabitLogRepository
import com.taskmanager.domain.repository.HabitRepository
import com.taskmanager.domain.repository.PomodoroSessionRepository
import com.taskmanager.domain.repository.ProjectRepository
import com.taskmanager.domain.repository.SubtaskRepository
import com.taskmanager.domain.repository.TagRepository
import com.taskmanager.domain.repository.TaskRepository
import com.taskmanager.domain.repository.UserStatsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(impl: ProjectRepositoryImpl): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(impl: TagRepositoryImpl): TagRepository

    @Binds
    @Singleton
    abstract fun bindUserStatsRepository(impl: UserStatsRepositoryImpl): UserStatsRepository

    @Binds
    @Singleton
    abstract fun bindSubtaskRepository(impl: SubtaskRepositoryImpl): SubtaskRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindHabitLogRepository(impl: HabitLogRepositoryImpl): HabitLogRepository

    @Binds
    @Singleton
    abstract fun bindPomodoroSessionRepository(impl: PomodoroSessionRepositoryImpl): PomodoroSessionRepository
}
