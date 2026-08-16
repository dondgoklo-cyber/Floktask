package com.taskmanager.di

import com.taskmanager.data.backup.BackupRepositoryImpl
import com.taskmanager.data.repository.ProjectRepositoryImpl
import com.taskmanager.data.repository.TagRepositoryImpl
import com.taskmanager.data.repository.TaskRepositoryImpl
import com.taskmanager.domain.repository.BackupRepository
import com.taskmanager.domain.repository.ProjectRepository
import com.taskmanager.domain.repository.TagRepository
import com.taskmanager.domain.repository.TaskRepository
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
    abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository
}
