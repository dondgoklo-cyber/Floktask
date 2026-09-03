package com.taskmanager.test

import com.taskmanager.domain.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing test dependencies.
 * Provides TestLogger as the Logger implementation for unit tests.
 * 
 * Part of Phase 1 architecture stabilization - Task #0.1
 */
@Module
@InstallIn(SingletonComponent::class)
object TestModule {
    @Provides
    @Singleton
    fun provideLogger(): Logger = TestLogger()
}
